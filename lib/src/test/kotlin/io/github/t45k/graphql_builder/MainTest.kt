package io.github.t45k.graphql_builder

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun noArg() {
        val query = buildQuery(SampleQuery::noArg) {
            output = {
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
        val query = buildQuery(
            SampleQuery::withArg,
            SampleInput(1)
        ) {
            output = {
                ::int.asField()
            }
        }
        assertEquals(
            """
                |{
                |  withArg(
                |    input: {
                |      int: 1
                |    }
                |  ) {
                |    int
                |  }
                |}
            """.trimMargin(), query
        )
    }
}