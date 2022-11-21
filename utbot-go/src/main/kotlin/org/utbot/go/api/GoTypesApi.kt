package org.utbot.go.api

import org.utbot.framework.plugin.api.ConstructorId
import org.utbot.framework.plugin.api.FieldId
import org.utbot.framework.plugin.api.go.GoClassId
import org.utbot.framework.plugin.api.go.GoStructConstructorId

/**
 * Represents real Go type.
 *
 * Note that unique identifier of GoTypeId (as for any children of GoClassId) is its name.
 */
open class GoTypeId(
    name: String,
    val implementsError: Boolean = false,
) : GoClassId(name) {
    override val simpleName: String
        get() = name
}

class GoStructTypeId(
    name: String,
    implementsError: Boolean,
    val fields: List<FieldId>,
) : GoTypeId(name, implementsError) {
    override val allConstructors: Sequence<ConstructorId>
        get() = sequenceOf(GoStructConstructorId(this, fields))
}

class GoArrayTypeId(
    name: String,
    private val elementTypeId: GoTypeId,
    val length: Int
) : GoTypeId(name) {
    override val elementClassId: GoClassId
        get() = elementTypeId
}

// Wraps tuple of several types into one GoClassId. It helps to handle multiple result types of Go functions.
class GoSyntheticMultipleTypesId(val types: List<GoTypeId>) :
    GoClassId("synthetic_multiple_types${types.typesToString()}") {
    override fun toString(): String = types.typesToString()
}

private fun List<GoTypeId>.typesToString(): String = this.joinToString(separator = ", ", prefix = "(", postfix = ")")

// There is no void type in Go; therefore, this class solves function returns nothing case.
class GoSyntheticNoTypeId : GoClassId("")