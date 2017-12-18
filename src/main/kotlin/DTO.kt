import java.io.File
import java.util.*

data class User(val id: UUID, var displayName: String, var password: String, var email: String, var state: UserState, val faction: Faction)

data class Faction(val id: UUID, val displayName: String, val coin: Coin)

data class Coin(val id: UUID, val displayName: String, val icon: File)

data class UserState(var balance: Long, var lastSimulation : Long,/* val upgrades: MutableList<Upgrade>,*/ val generators: MutableMap<Generator, Int>)

//data class Upgrade(val id: UUID, val displayName: String, val icon: File)

data class Generator(val id: UUID, val displayName: String, val icon: File, val rate : Double)