package org.utbot.go.fuzzer.providers

import org.utbot.framework.plugin.api.ClassId
import org.utbot.framework.plugin.api.go.GoStructConstructorId
import org.utbot.framework.plugin.api.go.GoUtModel
import org.utbot.fuzzer.FuzzedMethodDescription
import org.utbot.fuzzer.FuzzedType
import org.utbot.fuzzer.FuzzedValue
import org.utbot.fuzzer.IdentityPreservingIdGenerator
import org.utbot.fuzzer.providers.ModelConstructor
import org.utbot.fuzzer.providers.RecursiveModelProvider
import org.utbot.go.api.GoStructTypeId
import org.utbot.go.api.GoTypeId
import org.utbot.go.api.GoUtStructModel
import org.utbot.go.util.goRequiredImports

class GoStructModelProvider(
    idGenerator: IdentityPreservingIdGenerator<Int>,
    recursionDepthLeft: Int = 2,
) : RecursiveModelProvider(idGenerator, recursionDepthLeft) {
    override fun newInstance(
        parentProvider: RecursiveModelProvider,
        constructor: ModelConstructor
    ): RecursiveModelProvider {
        val newInstance =
            GoStructModelProvider(parentProvider.idGenerator, parentProvider.recursionDepthLeft - 1)
        newInstance.copySettings(parentProvider)
        newInstance.branchingLimit = 1
        return newInstance
    }

    override fun generateModelConstructors(
        description: FuzzedMethodDescription,
        parameterIndex: Int,
        classId: ClassId
    ): Sequence<ModelConstructor> = sequence {
        val typeId = classId as? GoStructTypeId ?: return@sequence

        val constructors = typeId.allConstructors

        constructors.forEach { constructorId ->
            yield(ModelConstructor(constructorId.parameters.map { classId -> FuzzedType(classId) }) {
                toGoUtStructModel(constructorId as GoStructConstructorId, it)
            })
        }
    }

    private fun toGoUtStructModel(constructorId: GoStructConstructorId, params: List<FuzzedValue>): FuzzedValue {
        return GoUtStructModel(
            constructorId.fields.zip(params).map { (field, param) -> field.name to param.model as GoUtModel }.toList(),
            constructorId.classId as GoTypeId,
            params.goRequiredImports,
        ).fuzzed {
            summary =
                "%var% = ${constructorId.classId.simpleName}(${constructorId.parameters.joinToString { it.simpleName }})"
        }
    }
}
