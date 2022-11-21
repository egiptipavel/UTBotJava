package org.utbot.go.fuzzer.providers

import org.utbot.fuzzer.FuzzedMethodDescription
import org.utbot.fuzzer.FuzzedParameter
import org.utbot.fuzzer.ModelProvider
import org.utbot.fuzzer.ModelProvider.Companion.yieldValue
import org.utbot.go.api.GoStructTypeId
import org.utbot.go.api.GoUtStructModel

object GoDefaultStructProvider : ModelProvider {
    override fun generate(description: FuzzedMethodDescription): Sequence<FuzzedParameter> = sequence {
        description.parametersMap
            .asSequence()
            .forEach { (classId, indices) ->
                val typeId = classId as? GoStructTypeId ?: return@forEach
                val model = GoUtStructModel(listOf(), typeId, setOf())
                indices.forEach {
                    yieldValue(it, model.fuzzed { this.summary = "%var% = ${model.classId.simpleName}{}" })
                }
            }
    }
}
