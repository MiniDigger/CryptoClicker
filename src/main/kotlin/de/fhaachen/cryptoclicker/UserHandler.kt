package de.fhaachen.cryptoclicker

import org.mindrot.BCrypt
import de.fhaachen.cryptoclicker.DB.execQuery

class UserHandler(private val db: DB) {

    private var users: MutableList<User> = mutableListOf()

    init {
        createUser("test", "test@test.de", "123456", BITCOIN.name)
        getOrLoadUser(name2= "test")?.state?.generators?.put(GPU, 1)
        getOrLoadUser(name2= "test")?.state?.generators?.put(MAINFRAME, 1)
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
                    if(user != null) return@getOrLoadUser user
                    //language=MySQL
                    "SELECT * FROM cryptoclicker.User WHERE name = $name2".execQuery()
                }
                email2 != null -> {
                    val user = users.find { it.email == email2 }
                    if(user != null) return@getOrLoadUser user
                    //language=MySQL
                    "SELECT * FROM cryptoclicker.User WHERE email = $email2".execQuery()
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
        return BITCOIN
    }

    private fun getOrLoadState(stateId: Int): UserState {
        return UserState(stateId, 0.0, System.currentTimeMillis(), mutableListOf(), mutableMapOf())
    }

    fun saveUser(user: User) {

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