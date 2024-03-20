package io.github.t45k.graphql_builder

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import org.objenesis.ObjenesisStd
import java.util.StringJoiner
import java.util.concurrent.CompletableFuture

/* private */ const val INDENT_SIZE = 2

// TODO: add other primitives
/* private */ val fieldTypes = setOf(Int::class, Number::class, String::class, ID::class)

/* private */ val objectMapper = jacksonObjectMapper()

/* private */ fun String.prependIndent(indentSize: Int): String = this.split("\n")
    .mapIndexed { index, s -> if (index == 0) s else (" ".repeat(indentSize) + s) }
    .joinToString("\n")

data class InputParam(val name: String, val value: String)

/* private */ inline fun <reified Input> parseInput(
    callable: KCallable<*>,
    input: Input,
): InputParam {
    val inputParamName = (callable.parameters.find { it.type.classifier as? KClass<*> == Input::class }?.name
        ?: throw IllegalArgumentException("${Input::class.simpleName} cannot be found in property of ${callable.name}"))
    val inputParamValue = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(input)
        .replace("\"([a-zA-Z0-9_]+)\":".toRegex(), "$1:")
    return InputParam(inputParamName, inputParamValue)
}

inline fun <reified Output> buildQuery(
    queryFunction: KFunction<Output>,
    specifyFields: OutputBuilder<Output>.() -> Unit,
): String {
    val output = OutputBuilder<Output>().run {
        specifyFields()
        ObjenesisStd().newInstance(Output::class.java).output()
        getBuiltQuery()
    }
    return """
        |{
        |  ${queryFunction.name} {
        |    ${output.prependIndent(INDENT_SIZE * 2)}
        |  }
        |}
    """.trimMargin()
}

inline fun <reified Input, reified Output> buildQuery(
    queryFunction: KFunction<Output>,
    input: Input,
    specifyFields: OutputBuilder<Output>.() -> Unit,
): String {
    val (inputParamName, inputParamValue) = parseInput(queryFunction, input)
    val queryOutput = OutputBuilder<Output>().run {
        specifyFields()
        ObjenesisStd().newInstance(Output::class.java).output()
        getBuiltQuery()
    }
    return """
        |{
        |  ${queryFunction.name} (
        |    $inputParamName: ${inputParamValue.prependIndent(INDENT_SIZE * 2)} 
        |  ) {
        |    ${queryOutput.prependIndent(INDENT_SIZE * 2)}
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

    @JvmName("asFieldOfCallable")
    inline fun <reified Field> KCallable<CompletableFuture<Field>>.asField() {
        if (Field::class in fieldTypes) buildingQuery.addWithIndent(this.name)
        else throw IllegalArgumentException("${Field::class.simpleName} cannot be used as field of GraphQL")
    }

    @JvmName("asField")
    inline fun <reified Field> KCallable<Field>.asField() {
        if (Field::class in fieldTypes) buildingQuery.addWithIndent(this.name)
        else throw IllegalArgumentException("${Field::class.simpleName} cannot be used as field of GraphQL")
    }

    @JvmName("asFieldWithInput")
    inline fun <reified Input, reified Field> KCallable<Field>.asField(input: Input) {
        val (inputParamName, inputParamValue) = parseInput(this, input)
        if (Field::class in fieldTypes) {
            buildingQuery.addWithIndent(
                """
                    |${this.name} (
                    |  $inputParamName: ${inputParamValue.prependIndent(INDENT_SIZE)}  
                    |)
                """.trimMargin()
            )
        } else throw IllegalArgumentException("${Field::class.simpleName} cannot be used as field of GraphQL")
    }

    inline fun <reified Object> KCallable<Object>.asObject(nested: Object.() -> Unit) {
        if (Object::class in fieldTypes) throw IllegalArgumentException("${Object::class.simpleName} cannot be used as object of GraphQL")
        else {
            buildingQuery.addWithIndent("$name {")
            currentIndent += INDENT_SIZE
            ObjenesisStd().newInstance(Object::class.java).nested()
            currentIndent -= INDENT_SIZE
            buildingQuery.addWithIndent("}")
        }
    }

    inline fun <reified Input, reified Object> KCallable<Object>.asObject(input: Input, nested: Object.() -> Unit) {
        val (inputParamName, inputParamValue) = parseInput(this, input)
        if (Object::class in fieldTypes) throw IllegalArgumentException("${Object::class.simpleName} cannot be used as object of GraphQL")
        else {
            buildingQuery.addWithIndent(
                """
                    |$name (
                    |  $inputParamName: ${inputParamValue.prependIndent(INDENT_SIZE)}
                    |) {
                """.trimMargin()
            )
            currentIndent += INDENT_SIZE
            ObjenesisStd().newInstance(Object::class.java).nested()
            currentIndent -= INDENT_SIZE
            buildingQuery.addWithIndent("}")
        }
    }

    // List
    // Completable
}

///////////////////////////////////////////
fun main() {
    println(buildQuery(SampleQueries::simple) {
        output = {
            ::fuga.asField(1)
        }
    })
}
