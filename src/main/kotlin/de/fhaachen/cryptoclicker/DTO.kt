package de.fhaachen.cryptoclicker

import java.io.File

data class User(val id: Int, val name: String, var password: String, var email: String, var state: UserState, val coin: Coin)

data class Coin(val id: Int, val name: String, val icon: File)

data class UserState(val id: Int, var balance: Double, var lastSimulation: Long, val upgrades: MutableList<Upgrade>, val generators: MutableMap<Generator, Int>)

data class Upgrade(val id: Int, val name: String, val icon: File)

data class Generator(val id: Int, val name: String, val icon: File, val rate: Double)