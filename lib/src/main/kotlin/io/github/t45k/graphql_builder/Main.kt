package io.github.t45k.graphql_builder

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.KSuspendFunction1
import kotlin.reflect.KSuspendFunction2
import kotlin.reflect.KSuspendFunction3
import kotlin.reflect.KSuspendFunction4
import org.objenesis.ObjenesisStd
import java.util.StringJoiner
import java.util.concurrent.CompletableFuture

// /* private */ means that it should be private value or function but made public due to `inline fun` limitation

/* private */ const val INDENT_SIZE = 2

// TODO: add other primitives
/* private */ val scalarTypes = setOf(Int::class, Number::class, String::class, ID::class)

/* private */ val objectMapper = jacksonObjectMapper()

/* private */ fun String.prependIndent(indentSize: Int): String = this.split("\n")
    .mapIndexed { index, s -> if (index == 0) s else (" ".repeat(indentSize) + s) }
    .joinToString("\n")

/* private */ inline fun <reified Input> formatInput(input: Input): String =
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(input)
        .replace("\"([a-zA-Z0-9_]+)\" :".toRegex(), "$1:")

/* private */ inline fun <reified Output> formatOutput(
    specifyFields: OutputBuilder<Output>.() -> Unit,
): String = OutputBuilder<Output>().run {
    specifyFields()
    ObjenesisStd().newInstance(Output::class.java).output()
    getBuiltQuery()
}

@JvmName("buildQueryWithoutInput")
inline fun <reified Output> buildQuery(
    queryFunction: KSuspendFunction1<*, Output>,
    specifyFields: OutputBuilder<Output>.() -> Unit,
): String {
    val formattedOutput = formatOutput(specifyFields)
    return """
        |{
        |  ${queryFunction.name} {
        |    ${formattedOutput.prependIndent(INDENT_SIZE * 2)}
        |  }
        |}
    """.trimMargin()
}

@JvmName("buildQueryWithInput")
inline fun <reified Input, reified Output> buildQuery(
    queryFunction: KSuspendFunction2<*, Input, Output>,
    input: Input,
    specifyFields: OutputBuilder<Output>.() -> Unit,
): String {
    val inputName = queryFunction.parameters[1].name!!
    val formattedInput = formatInput(input)
    val formattedOutput = formatOutput(specifyFields)
    return """
        |{
        |  ${queryFunction.name}(
        |    $inputName: ${formattedInput.prependIndent(INDENT_SIZE * 2)}
        |  ) {
        |    ${formattedOutput.prependIndent(INDENT_SIZE * 2)}
        |  }
        |}
    """.trimMargin()
}

class OutputBuilder<Output> {
    /* private */ val buildingQuery = StringJoiner("\n")
    /* private */ var currentIndent = 0
    lateinit var output: Output.() -> Unit

    fun getBuiltQuery(): String = buildingQuery.toString()

    /* private */ fun StringJoiner.addWithIndent(newElement: String): StringJoiner =
        this.add(newElement.prependIndent(currentIndent))

    /*
    Combination:
        definition: property, normal fun, or suspend fun
        input: absent or present
        parameter: none, single (it is input), double (first or second is input. another is often DataFetchingEnvironment), triple (first or second is input. the third one is often autowired use case)
        output: scalar, object, List, or CompletableFuture

    Limitation:
        - input can be used in normal or suspend fun definition
        - parameter is none when input is absent
        - CompletableFuture (by data loader) can be used in only normal fun
     */

    // TODO: 正しくディスパッチされるように、制限が厳しい順番にメソッドを書く

    // definition: normal fun, input: absent, output: scalar (Int) of CompletableFuture
    @JvmName("asField_definitionNormaFun_inputAbsent_outputScalarCompletableFuture")
    fun KFunction1<*, CompletableFuture<Int>>.asObject() {
        buildingQuery.addWithIndent(this.name)
    }

    // TODO: 全てのプリミティブ値について書く

    // definition: normal fun, input: absent, output: object of CompletableFuture
    @JvmName("asField_definitionAll_inputAbsent_outputObjectCompletableFuture")
    inline fun <reified Object> KFunction<CompletableFuture<Object>>.asObject(nested: Object.() -> Unit) {
        if (Object::class in scalarTypes) throw IllegalArgumentException("${Object::class.simpleName} cannot be used as object of GraphQL")
        else {
            buildingQuery.addWithIndent("$name {")
            currentIndent += INDENT_SIZE
            ObjenesisStd().newInstance(Object::class.java).nested()
            currentIndent -= INDENT_SIZE
            buildingQuery.addWithIndent("}")
        }
    }

    // definition: all, input: absent, output: scalar (Int)
    @JvmName("asField_definitionAll_inputAbsent_outputScalarInt")
    fun KCallable<Int>.asField() {
        buildingQuery.addWithIndent(this.name)
    }

    // definition: all, input: absent, output: scalar (String)
    @JvmName("asField_definitionAll_inputAbsent_outputScalarString")
    fun KCallable<String>.asField() {
        buildingQuery.addWithIndent(this.name)
    }

    // TODO: 全てのプリミティブ値について書く

    // definition: suspend fun, input: present, parameter: single, output: scalar (Int)
    @JvmName("asField_definitionSuspend_inputPresent_parameterSingle_outputScalarInt")
    inline fun <reified Input> KSuspendFunction2<*, Input, Int>.asField(input: Input) {
        val inputName = this.parameters[0].name!!
        val formattedInput = formatInput(input)
        buildingQuery.addWithIndent(
            """
                |$name(
                |  $inputName: ${formattedInput.prependIndent(INDENT_SIZE)}
                |)
            """.trimMargin()
        )
    }

    // definition: suspend fun, input: present, parameter: double and first, output: scalar (Int)
    @JvmName("asField_definitionSuspend_inputPresent_parameterDoubleFirst_outputScalarInt")
    inline fun <reified Input> KSuspendFunction3<*, Input, *, Int>.asField(input: Input) {
        val inputName = this.parameters[0].name!!
        val formattedInput = formatInput(input)
        buildingQuery.addWithIndent(
            """
                |$name(
                |  $inputName: ${formattedInput.prependIndent(INDENT_SIZE)}
                |)
            """.trimMargin()
        )
    }

    // definition: suspend fun, input: present, parameter: double and second, output: scalar (Int)
    @JvmName("asField_definitionSuspend_inputPresent_parameterDoubleSecond_outputScalarInt")
    inline fun <reified Input> KSuspendFunction3<*, *, Input, Int>.asField(input: Input) {
        val inputName = this.parameters[1].name!!
        val formattedInput = formatInput(input)
        buildingQuery.addWithIndent(
            """
                |$name(
                |  $inputName: ${formattedInput.prependIndent(INDENT_SIZE)}
                |)
            """.trimMargin()
        )
    }

    // definition: suspend fun, input: present, parameter: triple and first, output: scalar (Int)
    @JvmName("asField_definitionSuspend_inputPresent_parameterTripleFirst_outputScalarInt")
    inline fun <reified Input> KSuspendFunction4<*, Input, *, *, Int>.asField(input: Input) {
        val inputName = this.parameters[0].name!!
        val formattedInput = formatInput(input)
        buildingQuery.addWithIndent(
            """
                |$name(
                |  $inputName: ${formattedInput.prependIndent(INDENT_SIZE)}
                |)
            """.trimMargin()
        )
    }

    // definition: suspend fun, input: present, parameter: triple and second, output: scalar (Int)
    @JvmName("asField_definitionSuspend_inputPresent_parameterTripleSecond_outputScalarInt")
    inline fun <reified Input> KSuspendFunction4<*, *, Input, *, Int>.asField(input: Input) {
        val inputName = this.parameters[0].name!!
        val formattedInput = formatInput(input)
        buildingQuery.addWithIndent(
            """
                |$name(
                |  $inputName: ${formattedInput.prependIndent(INDENT_SIZE)}
                |)
            """.trimMargin()
        )
    }

    // definition: all, input: absent, output: List
    @JvmName("asField_definitionAll_inputAbsent_outputList")
    inline fun <reified Object> KCallable<List<Object>>.asObject(nested: Object.() -> Unit) {
        if (Object::class in scalarTypes) throw IllegalArgumentException("${Object::class.simpleName} cannot be used as object of GraphQL")
        else {
            buildingQuery.addWithIndent("$name {")
            currentIndent += INDENT_SIZE
            ObjenesisStd().newInstance(Object::class.java).nested()
            currentIndent -= INDENT_SIZE
            buildingQuery.addWithIndent("}")
        }
    }

    // definition: all, input: absent, output: object
    @JvmName("asField_definitionAll_inputAbsent_outputObject")
    inline fun <reified Object> KCallable<Object>.asObject(nested: Object.() -> Unit) {
        if (Object::class in scalarTypes) throw IllegalArgumentException("${Object::class.simpleName} cannot be used as object of GraphQL")
        else {
            buildingQuery.addWithIndent("$name {")
            currentIndent += INDENT_SIZE
            ObjenesisStd().newInstance(Object::class.java).nested()
            currentIndent -= INDENT_SIZE
            buildingQuery.addWithIndent("}")
        }
    }

    // List
    // Completable
}
