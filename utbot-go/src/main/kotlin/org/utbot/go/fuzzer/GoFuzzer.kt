package org.utbot.go.fuzzer

import org.utbot.fuzzer.FuzzedValue
import org.utbot.fuzzer.ModelProvider
import org.utbot.fuzzer.ReferencePreservingIntIdGenerator
import org.utbot.fuzzer.fuzz
import org.utbot.go.api.GoUtFunction
import org.utbot.go.fuzzer.providers.GoArrayModelProvider
import org.utbot.go.fuzzer.providers.GoDefaultStructProvider
import org.utbot.go.fuzzer.providers.GoPrimitivesModelProvider
import org.utbot.go.fuzzer.providers.GoStructModelProvider

object GoFuzzer {

    fun goFuzzing(function: GoUtFunction): Sequence<List<FuzzedValue>> {

        /**
         * Unit test generation for functions or methods with no parameters can be useful:
         * one can fixate panic behaviour or its absence.
         */
        if (function.parameters.isEmpty()) {
            return sequenceOf(emptyList())
        }

        // TODO: add more ModelProvider-s
        val modelProviderWithFallback = ModelProvider.of(
            GoPrimitivesModelProvider,
            GoStructModelProvider(ReferencePreservingIntIdGenerator(0), recursionDepthLeft = 2).apply {
                modelProviderForRecursiveCalls = ModelProvider.of(GoPrimitivesModelProvider)
                    .with(GoStructModelProvider(idGenerator, recursionDepthLeft - 1))
                    .with(GoArrayModelProvider(ReferencePreservingIntIdGenerator(0), recursionDepthLeft - 1))
                fallbackProvider = GoDefaultStructProvider
            },
            GoArrayModelProvider(ReferencePreservingIntIdGenerator(0), recursionDepthLeft = 2).apply {
                modelProviderForRecursiveCalls = ModelProvider.of(GoPrimitivesModelProvider)
                    .with(GoStructModelProvider(idGenerator, recursionDepthLeft - 1))
                    .with(GoArrayModelProvider(ReferencePreservingIntIdGenerator(0), recursionDepthLeft - 1))
                fallbackProvider = GoDefaultStructProvider
            }
        )

        return fuzz(function.toFuzzedMethodDescription(), modelProviderWithFallback)
    }

}