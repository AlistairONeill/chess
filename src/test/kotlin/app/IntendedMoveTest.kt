package app

import domain.*
import games.ExampleGames
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class IntendedMoveTest {

    @Test
    fun `Can parse Kasparov vs Topalov`() {
        ExampleGames.KasparovTopalov.forEach { (notation, intention) ->
            assertThat(
                "Failed to parse: [$notation]",
                Move(notation).parse(),
                equalTo(
                    intention
                )
            )
        }
    }
}