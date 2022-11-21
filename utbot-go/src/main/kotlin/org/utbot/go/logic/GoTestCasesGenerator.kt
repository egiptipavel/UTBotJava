package org.utbot.go.logic

import org.utbot.go.api.GoUtFile
import org.utbot.go.api.GoUtFunction
import org.utbot.go.api.GoUtFuzzedFunction
import org.utbot.go.api.GoUtFuzzedFunctionTestCase
import org.utbot.go.executor.GoFuzzedFunctionsExecutor
import org.utbot.go.fuzzer.GoFuzzer

object GoTestCasesGenerator {

    fun generateTestCasesForGoSourceFileFunctions(
        sourceFile: GoUtFile,
        functions: List<GoUtFunction>,
        goExecutableAbsolutePath: String,
        eachExecutionTimeoutsMillisConfig: EachExecutionTimeoutsMillisConfig,
    ): List<GoUtFuzzedFunctionTestCase> {
        val fuzzedFunctions = functions.map { function ->
            GoFuzzer.goFuzzing(function = function).shuffled().take(5).map { fuzzedParametersValues ->
                GoUtFuzzedFunction(function, fuzzedParametersValues)
            }.toList()
        }.flatten()

        return GoFuzzedFunctionsExecutor.executeGoSourceFileFuzzedFunctions(
            sourceFile,
            fuzzedFunctions,
            goExecutableAbsolutePath,
            eachExecutionTimeoutsMillisConfig
        ).map { (fuzzedFunction, executionResult) ->
            GoUtFuzzedFunctionTestCase(fuzzedFunction, executionResult)
        }
    }

}