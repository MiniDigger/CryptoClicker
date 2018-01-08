import org.mindrot.BCrypt

class UserHandler(private val db: DB) {

    private var users: MutableMap<String, User> = mutableMapOf()

    init {
        createUser("test", "test@test.de", "123456", BITCOIN.name)
        getUserByName("test")?.state?.generators?.put(GPU, 1)
        getUserByName("test")?.state?.generators?.put(MAINFRAME, 1)
    }

    fun loadUser(name: String): User? {
        return null
    }

    fun saveUser(user: User) {

    }

    fun getUserByName(name: String): User? = users[name]

    fun getUserByEmail(email: String): User? = users.values.find { user -> user.email == email }

    fun createUser(name: String, email: String, pw: String, coinName: String): String {
        if (getUserByName(name) != null) return "name taken"
        if (getUserByEmail(email) != null) return "email taken"
        val coin = getCoin(coinName) ?: return "unknown coin"
        val state = UserState(1, 0.0, System.currentTimeMillis(), mutableListOf(), mutableMapOf())
        val user = User(1, name, BCrypt.hashpw(pw, BCrypt.gensalt()), email, state, coin)
        users.put(name, user)
        println("Registered new User: $user")
        return "success"
    }

    fun login(email: String, password: String): Boolean {
        val user = getUserByEmail(email) ?: return false
        return BCrypt.checkpw(password, user.password)
    }
}