package io.github.t45k.graphql_builder

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun noArg() {
        val query = graphQLBuilder {
            SampleQuery::noArg.asQuery {
                ::int.asField()
            }
        }
        assertEquals(
            """
                |{
                |  noArg {
                |    int
                |  }
                |}
            """.trimMargin(), query
        )
    }

    @Test
    fun withArg() {
        val query = graphQLBuilder {
            SampleQuery::withArg.asQuery(SampleInput(1)) {
                ::int.asField()
            }
        }
        assertEquals(
            """
                |{
                |  withArg(input: {
                |    int: 1
                |  }) {
                |    int
                |  }
                |}
            """.trimMargin(), query
        )
    }

    @Test
    fun compound() {
        val query = graphQLBuilder {
            SampleQuery::withArg.asQuery(SampleInput(1)) {
                ::intWithoutParam.asField()
                ::objects.asObject {
                    ::int.asField()
                    ::intWithParam2.asField(SampleInput(2))
                }
            }
        }
        assertEquals(
            """
                |{
                |  withArg(input: {
                |    int: 1
                |  }) {
                |    intWithoutParam
                |    objects {
                |      int
                |      intWithParam2(input: {
                |        int: 2
                |      })
                |    }
                |  }
                |}
            """.trimMargin(),
            query
        )
    }
}