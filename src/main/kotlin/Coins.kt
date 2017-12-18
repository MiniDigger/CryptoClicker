import java.io.File

val BITCOIN = Coin("Bitcoin", File("bitcoin.png"))
val ETHERIUM = Coin("Etherium", File("bitcoin.png"))
val LITECOIN = Coin("LiteCoin", File("litecoin.png"))

private val coins: Map<String, Coin> = mapOf(
        BITCOIN.name to BITCOIN,
        ETHERIUM.name to ETHERIUM,
        LITECOIN.name to LITECOIN
)

fun getCoin(name: String): Coin? = coins[name]