package app

import domain.*
import games.ExampleGames
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class IntendedMoveTest {

    @Test
    fun `Can parse moves in Example Games`() {
        ExampleGames.allExampleGames.forEach { game ->
            game.moves.forEach { (move, intention) ->
                if (intention != null) {
                    assertThat(
                        "Failed to parse: [${move.notation}]",
                        move.parse(),
                        equalTo(
                            intention
                        )
                    )
                }
            }
        }
    }
}