package org.utbot.go

import org.utbot.go.api.GoUtFile
import org.utbot.go.api.GoUtFunction
import org.utbot.go.api.GoUtFuzzedFunction
import org.utbot.go.api.GoUtFuzzedFunctionTestCase
import org.utbot.go.executor.GoFuzzedFunctionsExecutor
import org.utbot.go.fuzzer.GoFuzzer
import org.utbot.go.logic.EachExecutionTimeoutsMillisConfig
import java.util.stream.IntStream
import kotlin.streams.toList

class GoEngine(
    private val methodUnderTest: GoUtFunction,
    private val sourceFile: GoUtFile,
    private val goExecutableAbsolutePath: String,
    private val eachExecutionTimeoutsMillisConfig: EachExecutionTimeoutsMillisConfig
) {
    fun fuzzing(): Sequence<GoUtFuzzedFunctionTestCase> = sequence {
        val fuzzedFunctions = GoFuzzer.goFuzzing(methodUnderTest)
            .map { fuzzedParametersValues -> GoUtFuzzedFunction(methodUnderTest, fuzzedParametersValues) }

        val needToCoverLines =
            IntStream.rangeClosed(1, methodUnderTest.numberOfAllStatements).toList().toMutableSet()

        fuzzedFunctions.chunked(500).forEach { partOfFuzzedFunctions ->
            GoFuzzedFunctionsExecutor.executeGoSourceFileFuzzedFunctions(
                sourceFile,
                partOfFuzzedFunctions,
                goExecutableAbsolutePath,
                eachExecutionTimeoutsMillisConfig,
            ).forEach { (fuzzedFunction, executionResult) ->
                if (needToCoverLines.intersect(executionResult.trace.toSet()).isNotEmpty()) {
                    needToCoverLines.removeAll(executionResult.trace.toSet())
                    yield(GoUtFuzzedFunctionTestCase(fuzzedFunction, executionResult))
                }
                if (needToCoverLines.isEmpty()) {
                    return@sequence
                }
            }
        }
    }
}