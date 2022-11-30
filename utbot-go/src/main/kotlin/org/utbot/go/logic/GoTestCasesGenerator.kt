package org.utbot.go.logic

import org.utbot.go.GoEngine
import org.utbot.go.api.GoUtFile
import org.utbot.go.api.GoUtFunction
import org.utbot.go.api.GoUtFuzzedFunctionTestCase

object GoTestCasesGenerator {

    fun generateTestCasesForGoSourceFileFunctions(
        sourceFile: GoUtFile,
        functions: List<GoUtFunction>,
        goExecutableAbsolutePath: String,
        eachExecutionTimeoutsMillisConfig: EachExecutionTimeoutsMillisConfig,
    ): List<GoUtFuzzedFunctionTestCase> {
        return functions.map { function ->
            GoEngine(function, sourceFile, goExecutableAbsolutePath, eachExecutionTimeoutsMillisConfig).fuzzing()
                .toList()
        }.flatten()
    }

}