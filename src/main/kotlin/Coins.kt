import java.io.File

val BITCOIN = Coin(0, "Bitcoin", File("bitcoin.png"))
val ETHERIUM = Coin(1, "Etherium", File("bitcoin.png"))
val LITECOIN = Coin(2, "LiteCoin", File("litecoin.png"))

private val coins: Map<String, Coin> = mapOf(
        BITCOIN.name to BITCOIN,
        ETHERIUM.name to ETHERIUM,
        LITECOIN.name to LITECOIN
)

fun getCoin(name: String): Coin? = coins[name]