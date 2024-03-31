@file:Suppress("unused")

package io.github.t45k.graphql_builder

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KSuspendFunction1
import kotlin.reflect.KSuspendFunction2
import kotlin.reflect.KSuspendFunction3
import org.objenesis.ObjenesisStd
import java.util.StringJoiner
import java.util.concurrent.CompletableFuture

fun graphQLBuilder(body: GraphQLBuilder.() -> Unit): String =
    GraphQLBuilder().apply { body() }.buildingQuery.toString()

data class GraphQLInput<I>(val name: String, val value: I)

class GraphQLBuilder {
    companion object {
        const val INDENT_SIZE = 2
        val scalarTypes = setOf(Int::class, String::class, ID::class)
        val objectMapper = jacksonObjectMapper()
    }

    val buildingQuery = StringJoiner("\n")
    var currentIndent = 0

    fun StringJoiner.addWithIndent(element: String): StringJoiner {
        return buildingQuery.add(" ".repeat(currentIndent) + element)
    }

    fun String.prependIndent(indentSize: Int): String = this.split("\n")
        .mapIndexed { index, s -> if (index == 0) s else (" ".repeat(indentSize) + s) }
        .joinToString("\n")

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // asQuery without input KFunction1
    @JvmName("asQuery_KFunction1_Query__O_")
    inline fun <Q : Query, reified O> KFunction1<Q, O>.asQuery(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, buildOutput)
    }

    @JvmName("asQuery_KFunction1_Query__List_O__")
    inline fun <Q : Query, reified O> KFunction1<Q, List<O>>.asQuery(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, buildOutput)
    }

    @JvmName("KFunction1_Query__CompletableFuture_O__")
    inline fun <Q : Query, reified O> KFunction1<Q, CompletableFuture<O>>.asQuery(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, buildOutput)
    }

    // asQuery with input for KFunction2
    @JvmName("asQuery_KFunction2_Query__I__O_")
    inline fun <Q : Query, I, reified O> KFunction2<Q, I, O>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KFunction2_Query__I__List_O__")
    inline fun <Q : Query, I, reified O> KFunction2<Q, I, List<O>>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KFunction2_Query__I__CompletableFuture_O__")
    inline fun <Q : Query, I, reified O> KFunction2<Q, I, CompletableFuture<O>>.asQuery(
        input: I,
        buildOutput: O.() -> Unit
    ) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    // asQuery with input for KFunction3
    @JvmName("asQuery_KFunction3_Query__I_____O_")
    inline fun <Q : Query, I, reified O> KFunction3<Q, I, *, O>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KFunction3_Query__I_____List_O__")
    inline fun <Q : Query, I, reified O> KFunction3<Q, I, *, List<O>>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KFunction3_Query__I_____CompletableFuture_O__")
    inline fun <Q : Query, I, reified O> KFunction3<Q, I, *, CompletableFuture<O>>.asQuery(
        input: I,
        buildOutput: O.() -> Unit
    ) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KFunction3_Query_____I__O_")
    inline fun <Q : Query, I, reified O> KFunction3<Q, *, I, O>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KFunction3_Query_____I__List_O__")
    inline fun <Q : Query, I, reified O> KFunction3<Q, *, I, List<O>>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KFunction3_Query_____I__CompletableFuture_O__")
    inline fun <Q : Query, I, reified O> KFunction3<Q, *, I, CompletableFuture<O>>.asQuery(
        input: I,
        buildOutput: O.() -> Unit
    ) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    // asQuery without input for KSuspendFunction1
    @JvmName("asQuery_KSuspendFunction1_Query_O_")
    inline fun <Q : Query, reified O> KSuspendFunction1<Q, O>.asQuery(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, buildOutput)
    }

    @JvmName("asQuery_KSuspendFunction1_Query_List_O__")
    inline fun <Q : Query, reified O> KSuspendFunction1<Q, List<O>>.asQuery(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, buildOutput)
    }

    // asQuery with input for KSuspendFunction2
    @JvmName("asQuery_KSuspendFunction2_Query__I__O_")
    inline fun <Q : Query, I, reified O> KSuspendFunction2<Q, I, O>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KSuspendFunction2_Query__I__List_O__")
    inline fun <Q : Query, I, reified O> KSuspendFunction2<Q, I, List<O>>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    // asQuery with input for KSuspendFunction3
    @JvmName("asQuery_KSuspendFunction3_Query__I_____O_")
    inline fun <Q : Query, I, reified O> KSuspendFunction3<Q, I, *, O>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KSuspendFunction3_Query__I_____List_O__")
    inline fun <Q : Query, I, reified O> KSuspendFunction3<Q, I, *, List<O>>.asQuery(
        input: I,
        buildOutput: O.() -> Unit
    ) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KSuspendFunction3_Query_____I__O_")
    inline fun <Q : Query, I, reified O> KSuspendFunction3<Q, *, I, O>.asQuery(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    @JvmName("asQuery_KSuspendFunction3_Query_____I__List_O__")
    inline fun <Q : Query, I, reified O> KSuspendFunction3<Q, *, I, List<O>>.asQuery(
        input: I,
        buildOutput: O.() -> Unit
    ) {
        require(O::class !in scalarTypes) { "" }
        handleQuery(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // asMutation

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // asField without input
    @JvmName("asField_KCallable_O_")
    inline fun <reified O> KCallable<O>.asField() {
        require(O::class in scalarTypes) { "" }
        handleField(name)
    }

    @JvmName("asField_KCallable_List_O__")
    inline fun <reified O> KCallable<List<O>>.asField() {
        require(O::class in scalarTypes) { "" }
        handleField(name)
    }

    @JvmName("asField_KCallable_CompletableFuture_O__")
    inline fun <reified O> KCallable<CompletableFuture<O>>.asField() {
        require(O::class in scalarTypes) { "" }
        handleField(name)
    }

    // asFiled with input for KFunction1
    @JvmName("asField_KFunction1_I__O_")
    inline fun <I, reified O> KFunction1<I, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction1_I__List_O__")
    inline fun <I, reified O> KFunction1<I, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction1_I__CompletableFuture_O__")
    inline fun <I, reified O> KFunction1<I, CompletableFuture<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    // asField with input for KFunction2
    @JvmName("asField_KFunction2_I_____O_")
    inline fun <I, reified O> KFunction2<I, *, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction2_I_____List_O__")
    inline fun <I, reified O> KFunction2<I, *, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction2_I_____CompletableFuture_O__")
    inline fun <I, reified O> KFunction2<I, *, CompletableFuture<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction2____I__O_")
    inline fun <I, reified O> KFunction2<*, I, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KFunction2____I__List_O__")
    inline fun <I, reified O> KFunction2<*, I, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KFunction2____I__CompletableFuture_O__")
    inline fun <I, reified O> KFunction2<*, I, CompletableFuture<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    // asField with input for KFunction3
    @JvmName("asField_KFunction3_I________O_")
    inline fun <I, reified O> KFunction3<I, *, *, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction3_I________List_O__")
    inline fun <I, reified O> KFunction3<I, *, *, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction3_I________CompletableFuture_O__")
    inline fun <I, reified O> KFunction3<I, *, *, CompletableFuture<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KFunction3____I_____O_")
    inline fun <I, reified O> KFunction3<*, I, *, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KFunction3____I_____List_O__")
    inline fun <I, reified O> KFunction3<*, I, *, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KFunction3____I_____CompletableFuture_O__")
    inline fun <I, reified O> KFunction3<*, I, *, CompletableFuture<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KFunction3_______I__O_")
    inline fun <I, reified O> KFunction3<*, *, I, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[2].name!!, input))
    }

    @JvmName("asField_KFunction3_______I__List_O__")
    inline fun <I, reified O> KFunction3<*, *, I, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[2].name!!, input))
    }

    @JvmName("asField_KFunction3_______I__CompletableFuture_O__")
    inline fun <I, reified O> KFunction3<*, *, I, CompletableFuture<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[2].name!!, input))
    }

    // asFiled with input for KSuspendFunction1
    @JvmName("asField_KSuspendFunction1_I__O_")
    inline fun <I, reified O> KSuspendFunction1<I, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KSuspendFunction1_I__List_O__")
    inline fun <I, reified O> KSuspendFunction1<I, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    // asField with input for KSuspendFunction2
    @JvmName("asField_KSuspendFunction2_I_____O_")
    inline fun <I, reified O> KSuspendFunction2<I, *, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KSuspendFunction2_I_____List_O__")
    inline fun <I, reified O> KSuspendFunction2<I, *, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KSuspendFunction2____I__O_")
    inline fun <I, reified O> KSuspendFunction2<*, I, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KSuspendFunction2____I__List_O__")
    inline fun <I, reified O> KSuspendFunction2<*, I, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    // asField with input for KSuspendFunction3
    @JvmName("asField_KSuspendFunction3_I________O_")
    inline fun <I, reified O> KSuspendFunction3<I, *, *, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KSuspendFunction3_I________List_O__")
    inline fun <I, reified O> KSuspendFunction3<I, *, *, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[0].name!!, input))
    }

    @JvmName("asField_KSuspendFunction3____I_____O_")
    inline fun <I, reified O> KSuspendFunction3<*, I, *, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KSuspendFunction3____I_____List_O__")
    inline fun <I, reified O> KSuspendFunction3<*, I, *, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[1].name!!, input))
    }

    @JvmName("asField_KSuspendFunction3_______I__O_")
    inline fun <I, reified O> KSuspendFunction3<*, *, I, O>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[2].name!!, input))
    }

    @JvmName("asField_KSuspendFunction3_______I__List_O__")
    inline fun <I, reified O> KSuspendFunction3<*, *, I, List<O>>.asField(input: I) {
        require(O::class in scalarTypes) { "" }
        handleField(name, GraphQLInput(this.parameters[2].name!!, input))
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // asObject
    @JvmName("asObject_KCallable_O_")
    inline fun <reified O> KCallable<O>.asObject(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, buildOutput)
    }

    @JvmName("asObject_KCallable_List_O__")
    inline fun <reified O> KCallable<List<O>>.asObject(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, buildOutput)
    }

    @JvmName("asObject_KCallable_CompletableFuture_O__")
    inline fun <reified O> KCallable<CompletableFuture<O>>.asObject(buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, buildOutput)
    }

    // asObject with input for KFunction1
    @JvmName("asObject_KFunction1_I_O_")
    inline fun <I, reified O> KFunction1<I, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction1_I_List_O__")
    inline fun <I, reified O> KFunction1<I, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction1_I_CompletableFuture_O__")
    inline fun <I, reified O> KFunction1<I, CompletableFuture<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    // asObject with input for KFunction2
    @JvmName("asObject_KFunction2_I_____O_")
    inline fun <I, reified O> KFunction2<I, *, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction2_I_____List_O__")
    inline fun <I, reified O> KFunction2<I, *, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction2_I_____CompletableFuture_O__")
    inline fun <I, reified O> KFunction2<I, *, CompletableFuture<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction2____I__O_")
    inline fun <I, reified O> KFunction2<*, I, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction2____I__List_O__")
    inline fun <I, reified O> KFunction2<*, I, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction2____I__CompletableFuture_O__")
    inline fun <I, reified O> KFunction2<*, I, CompletableFuture<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    // asObject with input for KFunction3
    @JvmName("asObject_KFunction3_I________O_")
    inline fun <I, reified O> KFunction3<I, *, *, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3_I________List_O__")
    inline fun <I, reified O> KFunction3<I, *, *, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3_I________CompletableFuture_O__")
    inline fun <I, reified O> KFunction3<I, *, *, CompletableFuture<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3____I_____O_")
    inline fun <I, reified O> KFunction3<*, I, *, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3____I_____List_O__")
    inline fun <I, reified O> KFunction3<*, I, *, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3____I_____CompletableFuture_O__")
    inline fun <I, reified O> KFunction3<*, I, *, CompletableFuture<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3_______I__O_")
    inline fun <I, reified O> KFunction3<*, *, I, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3_______I__List_O__")
    inline fun <I, reified O> KFunction3<*, *, I, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    @JvmName("asObject_KFunction3_______I__CompletableFuture_O__")
    inline fun <I, reified O> KFunction3<*, *, I, CompletableFuture<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    // asObject with input for KSuspendFunction1
    @JvmName("asObject_KSuspendFunction1_I_O_")
    inline fun <I, reified O> KSuspendFunction1<I, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction1_I_List_O__")
    inline fun <I, reified O> KSuspendFunction1<I, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    // asObject with input for KSuspendFunction2
    @JvmName("asObject_KSuspendFunction2_I_____O_")
    inline fun <I, reified O> KSuspendFunction2<I, *, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction2_I_____List_O__")
    inline fun <I, reified O> KSuspendFunction2<I, *, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction2____I__O_")
    inline fun <I, reified O> KSuspendFunction2<*, I, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction2____I__List_O__")
    inline fun <I, reified O> KSuspendFunction2<*, I, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    // asObject with input for KSuspendFunction3
    @JvmName("asObject_KSuspendFunction3_I________O_")
    inline fun <I, reified O> KSuspendFunction3<I, *, *, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction3_I________List_O__")
    inline fun <I, reified O> KSuspendFunction3<I, *, *, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[0].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction3____I_____O_")
    inline fun <I, reified O> KSuspendFunction3<*, I, *, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction3____I_____List_O__")
    inline fun <I, reified O> KSuspendFunction3<*, I, *, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[1].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction3_______I__O_")
    inline fun <I, reified O> KSuspendFunction3<*, *, I, O>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    @JvmName("asObject_KSuspendFunction3_______I__List_O__")
    inline fun <I, reified O> KSuspendFunction3<*, *, I, List<O>>.asObject(input: I, buildOutput: O.() -> Unit) {
        require(O::class !in scalarTypes) { "" }
        handleObject(this.name, GraphQLInput(this.parameters[2].name!!, input), buildOutput)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    inline fun <T : Any, reified O : T> T.asInlineFragment(clazz: KClass<O>, buildOutput: O.() -> Unit) {
        buildingQuery.addWithIndent("... on ${O::class.simpleName} {")
        currentIndent += INDENT_SIZE
        instantiate<O>().buildOutput()
        currentIndent -= INDENT_SIZE
        buildingQuery.addWithIndent("}")
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // common
    fun <I> formatInput(input: I): String =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(input)
            .replace("\"([a-zA-Z0-9_]+)\" :".toRegex(), "$1:")
            .split("\n")
            .mapIndexed { index, line -> if (index == 0) line else (" ".repeat(currentIndent) + line) }
            .joinToString("\n")

    inline fun <reified O> handleQuery(name: String, buildOutput: O.() -> Unit) {
        buildingQuery.addWithIndent("{")
        currentIndent += INDENT_SIZE
        handleObject<O>(name, buildOutput)
        currentIndent -= INDENT_SIZE
        buildingQuery.addWithIndent("}")
    }

    inline fun <I, reified O> handleQuery(name: String, input: GraphQLInput<I>, buildOutput: O.() -> Unit) {
        buildingQuery.addWithIndent("{")
        currentIndent += INDENT_SIZE
        handleObject(name, input, buildOutput)
        currentIndent -= INDENT_SIZE
        buildingQuery.addWithIndent("}")
    }

    fun handleField(name: String) {
        buildingQuery.addWithIndent(name)
    }

    fun <I> handleField(name: String, input: GraphQLInput<I>) {
        val formattedInput = formatInput(input.value)
        buildingQuery.addWithIndent("$name(${input.name}: $formattedInput)")
    }

    inline fun <reified O> handleObject(name: String, buildOutput: O.() -> Unit) {
        buildingQuery.addWithIndent("$name {")
        currentIndent += INDENT_SIZE
        instantiate<O>().buildOutput()
        currentIndent -= INDENT_SIZE
        buildingQuery.addWithIndent("}")
    }

    inline fun <I, reified O> handleObject(name: String, input: GraphQLInput<I>, buildOutput: O.() -> Unit) {
        val formattedInput = formatInput(input.value)
        buildingQuery.addWithIndent("$name(${input.name}: $formattedInput) {")
        currentIndent += INDENT_SIZE
        instantiate<O>().buildOutput()
        currentIndent -= INDENT_SIZE
        buildingQuery.addWithIndent("}")
    }

    inline fun <reified O> instantiate(): O =
        if (O::class.isSealed) {
            ObjenesisStd().newInstance(O::class.sealedSubclasses.first().java)
        } else {
            ObjenesisStd().newInstance(O::class.java)
        }
}
