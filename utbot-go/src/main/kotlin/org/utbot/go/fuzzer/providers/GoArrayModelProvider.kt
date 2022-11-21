package org.utbot.go.fuzzer.providers

import org.utbot.framework.plugin.api.ClassId
import org.utbot.framework.plugin.api.go.GoUtModel
import org.utbot.fuzzer.FuzzedMethodDescription
import org.utbot.fuzzer.FuzzedType
import org.utbot.fuzzer.FuzzedValue
import org.utbot.fuzzer.IdentityPreservingIdGenerator
import org.utbot.fuzzer.providers.ModelConstructor
import org.utbot.fuzzer.providers.RecursiveModelProvider
import org.utbot.go.api.GoArrayTypeId
import org.utbot.go.api.GoUtArrayModel
import org.utbot.go.util.goRequiredImports

class GoArrayModelProvider(
    idGenerator: IdentityPreservingIdGenerator<Int>,
    recursionDepthLeft: Int = 2,
) : RecursiveModelProvider(idGenerator, recursionDepthLeft) {
    override fun newInstance(
        parentProvider: RecursiveModelProvider,
        constructor: ModelConstructor
    ): RecursiveModelProvider {
        val newInstance =
            GoArrayModelProvider(parentProvider.idGenerator, parentProvider.recursionDepthLeft - 1)
        newInstance.copySettings(parentProvider)
        newInstance.branchingLimit = 1
        return newInstance
    }

    override fun generateModelConstructors(
        description: FuzzedMethodDescription,
        parameterIndex: Int,
        classId: ClassId
    ): Sequence<ModelConstructor> = sequence {
        val typeId = classId as? GoArrayTypeId ?: return@sequence
        val length = typeId.length
        yield(ModelConstructor(listOf(FuzzedType(typeId.elementClassId)), repeat = length) { values ->
            createFuzzedArrayModel(typeId, length, values)
        })
    }

    private fun createFuzzedArrayModel(
        arrayTypeId: GoArrayTypeId,
        length: Int,
        values: List<FuzzedValue>
    ): FuzzedValue {
        return GoUtArrayModel(
            values.map { it.model as GoUtModel }.toList(),
            arrayTypeId,
            length,
            values.goRequiredImports
        ).fuzzed {
            summary = "%var% = [$length]${arrayTypeId.elementClassId.simpleName}"
        }
    }
}