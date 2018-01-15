package de.fhaachen.cryptoclicker

val BITCOIN = Coin(1, "Bitcoin", "bitcoin.png")
val ETHERIUM = Coin(2, "Etherium","bitcoin.png")
val LITECOIN = Coin(3, "LiteCoin","litecoin.png")

private val coins: Map<String, Coin> = mapOf(
        BITCOIN.name to BITCOIN,
        ETHERIUM.name to ETHERIUM,
        LITECOIN.name to LITECOIN
)

fun getCoin(name: String): Coin? = coins[name]