var interval = 1000L

fun catchUp(user: User) {
    val diff = System.currentTimeMillis() - user.state.lastSimulation
    val catchUp = (diff / interval).toInt()
    simulate(user, catchUp)
}

fun simulate(user: User, steps: Int) {
    for (i in 0..steps) {
        simulate(user)
    }
}

fun simulate(user: User) {
    user.state.lastSimulation = System.currentTimeMillis()

    for ((generator, count) in user.state.generators) {
        user.state.balance += (generator.rate * count).toLong()
    }
}