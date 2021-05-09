package domain

import domain.Player.*

enum class Player {
    White, Black
}

fun Player.other() = when (this) {
    White -> Black
    Black -> White
}