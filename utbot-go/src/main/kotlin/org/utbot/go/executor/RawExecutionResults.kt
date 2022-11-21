package org.utbot.go.executor

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import org.utbot.go.api.GoArrayTypeId
import org.utbot.go.api.GoStructTypeId
import org.utbot.go.api.GoTypeId
import org.utbot.go.api.util.goPrimitives
import org.utbot.go.api.util.isPrimitiveGoType
import kotlin.reflect.KClass

data class PrimitiveValue(
    override val type: String,
    override val value: String,
) : RawResultValue(type, value)

data class StructValue(
    override val type: String,
    override val value: List<FieldValue>
) : RawResultValue(type, value) {
    data class FieldValue(
        val name: String,
        val value: RawResultValue
    )
}

data class ArrayValue(
    override val type: String,
    val elementType: String,
    val length: Int,
    override val value: List<RawResultValue>
) : RawResultValue(type, value)

@TypeFor(field = "type", adapter = RawResultValueAdapter::class)
abstract class RawResultValue(open val type: String, open val value: Any)

fun RawResultValue.checkIsEqualTypes(resultType: GoTypeId): Boolean {
    if (this.type != resultType.simpleName) {
        return false
    }
    if (resultType.isPrimitiveGoType) {
        return this is PrimitiveValue
    }
    when (resultType) {
        is GoStructTypeId -> {
            val structValue = this as? StructValue ?: return false
            if (structValue.value.size != resultType.fields.size) {
                return false
            }
            structValue.value.zip(resultType.fields).forEach { (fieldValue, fieldId) ->
                when {
                    fieldValue.name != fieldId.name -> return false
                    !fieldValue.value.checkIsEqualTypes(fieldId.declaringClass as GoTypeId) -> return false
                }
            }
        }
        is GoArrayTypeId -> {
            val arrayValue = this as? ArrayValue ?: return false
            when {
                arrayValue.length != resultType.length -> return false
                arrayValue.elementType != resultType.elementClassId.simpleName -> return false
            }
            arrayValue.value.forEach { arrayElementValue ->
                if (!arrayElementValue.checkIsEqualTypes(resultType.elementClassId as GoTypeId)) {
                    return false
                }
            }
        }
        else -> return false
    }
    return true
}

class RawResultValueAdapter : TypeAdapter<RawResultValue> {
    override fun classFor(type: Any): KClass<out RawResultValue> {
        val nameOfType = type as String
        return when {
            nameOfType.startsWith("map[") -> error("Map result type not supported")
            nameOfType.startsWith("[]") -> error("Slice result type not supported")
            nameOfType.startsWith("[") -> ArrayValue::class
            goPrimitives.map { it.name }.contains(nameOfType) -> PrimitiveValue::class
            else -> StructValue::class
        }
    }
}

internal data class RawPanicMessage(
    val rawResultValue: RawResultValue,
    val implementsError: Boolean
)

internal data class RawExecutionResult(
    val functionName: String,
    val timeoutExceeded: Boolean,
    val resultRawValues: List<RawResultValue>,
    val panicMessage: RawPanicMessage?,
)

internal data class RawExecutionResults(val results: List<RawExecutionResult>)