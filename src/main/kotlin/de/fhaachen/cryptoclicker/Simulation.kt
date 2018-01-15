package de.fhaachen.cryptoclicker

class Simulation(private val userHandler: UserHandler) {

    private val interval = 1000L

    fun status(userName: String?, recalc: String?): UserState {
        if (userName == null) throw IllegalArgumentException("de.fhaachen.cryptoclicker.User null")

        val user = userHandler.getOrLoadUser(name2 = userName) ?: throw IllegalArgumentException("Unknown user")
        val calc = recalc?.toBoolean() ?: false
        if (calc) {
            catchUp(user)
        }
        return user.state
    }

    fun catchUp(user: User) {
        val diff = System.currentTimeMillis() - user.state.lastSimulation
        val catchUp = (diff / interval).toInt()
        println("diff is $diff, catchup is $catchUp")
        simulate(user, catchUp)
    }

    fun simulate(user: User, steps: Int) {
        for (i in 1..steps) {
            simulate(user)
        }
    }

    fun simulate(user: User) {
        user.state.lastSimulation = System.currentTimeMillis()

        for ((generator, count) in user.state.generators) {
            user.state.balance += generator.rate * count
        }
    }
}