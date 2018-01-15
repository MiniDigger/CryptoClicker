package de.fhaachen.cryptoclicker

data class User(var id: Int, val name: String, var password: String, var email: String, var state: UserState, val coin: Coin)

data class Coin(var id: Int, val name: String, val icon: String)

data class UserState(var id: Int, var balance: Double, var lastSimulation: Long, val upgrades: MutableList<Upgrade>, val generators: MutableMap<Generator, Int>)

data class Upgrade(var id: Int, val name: String, val icon: String)

data class Generator(var id: Int, val name: String, val icon: String, val rate: Double)