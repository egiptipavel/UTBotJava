package org.utbot.go.logic

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.utbot.go.GoEngine
import org.utbot.go.api.*
import org.utbot.go.framework.api.go.GoTypeId
import kotlin.system.measureTimeMillis

val logger = KotlinLogging.logger {}

object GoTestCasesGenerator {

    fun generateTestCasesForGoSourceFileFunctions(
        sourceFile: GoUtFile,
        functions: List<GoUtFunction>,
        goExecutableAbsolutePath: String,
        eachExecutionTimeoutsMillisConfig: EachExecutionTimeoutsMillisConfig,
        timeoutExceededOrIsCanceled: (index: Int) -> Boolean
    ): List<GoUtFuzzedFunctionTestCase> = runBlocking {
        return@runBlocking functions.flatMapIndexed { index, function ->
            val testCases = mutableListOf<GoUtFuzzedFunctionTestCase>()
            if (timeoutExceededOrIsCanceled(index)) return@flatMapIndexed testCases
            val engine = GoEngine(
                function,
                sourceFile,
                goExecutableAbsolutePath,
                eachExecutionTimeoutsMillisConfig,
                { timeoutExceededOrIsCanceled(index) }
            )

            fun GoTypeId.containsGoStructTypeId(): Boolean = when (this) {
                is GoArrayTypeId -> this.elementTypeId.containsGoStructTypeId()
                is GoStructTypeId -> true
                else -> false
            }

            val flow =
                if (function.parameters.all { !it.type.containsGoStructTypeId() }) {
                    engine.fastFuzzing()
                } else {
                    engine.fuzzing()
                }
            logger.info { "Fuzzing for function [${function.name}] - started" }
            val totalFuzzingTime = measureTimeMillis {
                flow.catch {
                    logger.error { "Error in flow: ${it.message}" }
                }.collect { (fuzzedFunction, executionResult) ->
                    testCases.add(GoUtFuzzedFunctionTestCase(fuzzedFunction, executionResult))
                }
            }
            logger.info { "Fuzzing for function [${function.name}] - completed in $totalFuzzingTime ms" }
            logger.info { "Generated ${testCases.size} test cases"}
            testCases
        }
    }
}