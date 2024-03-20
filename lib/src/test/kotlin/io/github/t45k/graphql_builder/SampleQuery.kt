package io.github.t45k.graphql_builder

import java.util.concurrent.CompletableFuture

class SampleQuery {
    suspend fun noArg(): SampleOutput = TODO()
    suspend fun withArg(input: SampleInput): SampleOutput = TODO()
}

data class SampleInput(val int: Int)

data class SampleOutput(val int: Int) {
    suspend fun intWithoutParam(): Int = TODO()
    suspend fun intWithParam(input: SampleInput): Int = TODO()
    suspend fun intWithParam2(input: SampleInput, dummy: String): Int = TODO()
    suspend fun intWithParam3(input: SampleInput, dummy: String, dummy2: String): Int = TODO()
    suspend fun objects(): SampleOutput = TODO()
    suspend fun list(): List<Int> = TODO()
    fun dataLoader(): CompletableFuture<Int> = TODO()
}
