import org.mindrot.BCrypt

private val users: MutableMap<String, User> = mutableMapOf()

fun initUserHandler() {
    createUser("test", "test@test.de", "123456", BITCOIN.name)
    getUserByName("test")?.state?.generators?.put(GPU, 1)
}

fun getUserByName(name: String): User? = users[name]

fun getUserByEmail(email: String): User? = users.values.find { user -> user.email == email }

fun createUser(name: String, email: String, pw: String, coinName: String): String {
    if (getUserByName(name) != null) return "name taken"
    if (getUserByEmail(email) != null) return "email taken"
    val coin = getCoin(coinName) ?: return "unknown coin"
    val state = UserState(0, System.currentTimeMillis(), mutableMapOf())
    val user = User(name, BCrypt.hashpw(pw, BCrypt.gensalt()), email, state, coin)
    users.put(name, user)
    println("Registered new User: $user")
    return "success"
}

fun login(email: String, password: String): Boolean {
    val user = getUserByEmail(email) ?: return false
    return BCrypt.checkpw(password, user.password)
}