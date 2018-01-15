package de.fhaachen.cryptoclicker

import de.fhaachen.cryptoclicker.DB.exec
import de.fhaachen.cryptoclicker.DB.execQuery
import org.mindrot.BCrypt

class UserHandler(private val db: DB) {

    private var users: MutableList<User> = mutableListOf()

    init {
        createUser("test", "test@test.de", "123456", BITCOIN.name)
        getOrLoadUser(name2 = "test")?.state?.generators?.put(GPU, 1)
        getOrLoadUser(name2 = "test")?.state?.generators?.put(MAINFRAME, 1)
    }

    fun getOrLoadUser(id2: Int? = null, name2: String? = null, email2: String? = null): User? {
        try {
            val rs = when {
                id2 != null -> {
                    val user = users.find { it.id == id2 }
                    if (user != null) return@getOrLoadUser user
                    //language=MySQL
                    "SELECT * FROM cryptoclicker.User WHERE id = $id2".execQuery()
                }
                name2 != null -> {
                    val user = users.find { it.name == name2 }
                    if (user != null) return@getOrLoadUser user
                    //language=MySQL
                    "SELECT * FROM cryptoclicker.User WHERE name = '$name2'".execQuery()
                }
                email2 != null -> {
                    val user = users.find { it.email == email2 }
                    if (user != null) return@getOrLoadUser user
                    //language=MySQL
                    "SELECT * FROM cryptoclicker.User WHERE email = '$email2'".execQuery()
                }
                else -> throw IllegalArgumentException("Id and name and email are null?!")
            }
            if (!rs.next()) return null
            val user = User(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getString("email"),
                    getOrLoadState(rs.getInt("state_id")),
                    getOrLoadCoin(rs.getInt("coin")))
            users.add(user)
            return user
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    private fun getOrLoadCoin(coinId: Int): Coin {
        // TODO load coin
        return BITCOIN
    }

    private fun getOrLoadState(stateId: Int): UserState {
        //TODO load state
        return UserState(stateId, 0.0, System.currentTimeMillis(), mutableListOf(), mutableMapOf())
    }

    fun saveUser(user: User) {
        saveCoin(user.coin)
        saveState(user.state)
        //language=MySQL
        "INSERT INTO cryptoclicker.User(name, password, email, state_id, coin_id) VALUES ('${user.name}','${user.password}','${user.email}','${user.state.id}','${user.coin.id}') ON DUPLICATE KEY UPDATE name=VALUES(name), email=VALUES(email),password=VALUES(password),state_id=VALUES(state_id),coin_id=VALUES(state_id)".exec()
        user.id = DB.getGeneratedId()
    }

    fun saveState(userState: UserState) {
        //language=MySQL
        "INSERT INTO cryptoclicker.UserState(balance, lastSimulation) VALUES ('${userState.balance}','${userState.lastSimulation}') ON DUPLICATE KEY UPDATE balance=VALUES(balance),lastSimulation=VALUES(lastSimulation)".exec()
        userState.id = DB.getGeneratedId()
        userState.generators.forEach { gen, lvl ->
            saveGenerator(gen)
            //language=MySQL
            "INSERT INTO cryptoclicker.Generators(generator_id, state_id, level) VALUES ('${gen.id}', '${userState.id}', '$lvl') ON DUPLICATE KEY UPDATE generator_id=VALUES(generator_id),state_id=VALUES(state_id),level=VALUES(level)".exec()
        }
        userState.upgrades.forEach { upgrade ->
            saveUpgrade(upgrade)
            //language=MySQL
            "INSERT INTO cryptoclicker.Upgrades(upgrade_id, state_id) VALUES ('${upgrade.id}', '${userState.id}') ON DUPLICATE KEY UPDATE upgrade_id=VALUES(upgrade_id),state_id=VALUES(state_id)".exec()
        }
    }

    fun saveGenerator(generator: Generator) {
        //language=MySQL
        "INSERT INTO cryptoclicker.Generator(name, icon, rate) VALUES ('${generator.name}','${generator.icon}', '${generator.rate}') ON DUPLICATE KEY UPDATE name=VALUES(name),icon=VALUES(icon),rate=VALUES(rate)".exec()
        generator.id = DB.getGeneratedId()
    }

    fun saveUpgrade(upgrade: Upgrade) {
        //language=MySQL
        ("INSERT INTO cryptoclicker.Upgrade(name, icon) VALUES ('${upgrade.name}','${upgrade.icon}') ON DUPLICATE KEY UPDATE name=VALUES(name),icon=VALUES(icon)").exec()
        upgrade.id = DB.getGeneratedId()
    }

    fun saveCoin(coin: Coin) {
        //language=MySQL
        ("INSERT INTO cryptoclicker.Coin(name, icon) VALUES ('${coin.name}','${coin.icon}') ON DUPLICATE KEY UPDATE name=VALUES(name),icon=VALUES(icon)").exec()
        val generatedId = DB.getGeneratedId()
        if(generatedId != -1){
            //TODO fixme
            coin.id = generatedId
        }
    }

    fun createUser(name: String, email: String, pw: String, coinName: String): String {
        if (getOrLoadUser(name2 = name) != null) return "name taken"
        if (getOrLoadUser(email2 = email) != null) return "email taken"
        val coin = getCoin(coinName) ?: return "unknown coin"
        val state = UserState(1, 0.0, System.currentTimeMillis(), mutableListOf(), mutableMapOf())
        val user = User(1, name, BCrypt.hashpw(pw, BCrypt.gensalt()), email, state, coin)
        users.add(user)
        saveUser(user)
        println("Registered new User: $user")
        return "success"
    }

    fun login(email: String, password: String): Boolean {
        val user = getOrLoadUser(email2 = email) ?: return false
        return BCrypt.checkpw(password, user.password)
    }
}