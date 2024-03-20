package io.github.t45k.graphql_builder

@JvmInline
value class ID(val value: String) // alternative of GraphQL Kotlin ID

class SampleQueries {
    fun simple(): SimpleOutput = TODO()
    fun oneArg(input: SimpleInput): SimpleOutput = TODO()
    fun idArg(input: ID): SimpleOutput = TODO()
    fun enumArg(input: SimpleEnum): SimpleOutput = TODO()
}

data class SimpleOutput(val str: String) {
    fun fuga(dd: Int): Int = 1
}

data class SimpleInput(val str: String, val str2: String, val str3: String)

enum class SimpleEnum {
    FIRST, SECOND
}
