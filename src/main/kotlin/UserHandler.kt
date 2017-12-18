import java.util.*

class UserHandler {
    val users: MutableMap<UUID, User> = mutableMapOf()

    fun getUser(id: UUID): User? = users[id]
}