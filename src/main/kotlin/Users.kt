import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.BCrypt
import java.sql.ResultSet

private val users: MutableMap<String, User> = mutableMapOf()

fun initUserHandler() {
    createUser("test", "test@test.de", "123456", BITCOIN.name)
    getUserByName("test")?.state?.generators?.put(GPU, 1)
    getUserByName("test")?.state?.generators?.put(MAINFRAME, 1)

    Database.connect("jdbc:mysql://minidigger.me:3336/cryptoclicker", "com.mysql.jdbc.Driver", "cryptoclicker", "cryptoclicker")
    if (!dbExists()) createDB()
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

fun <T : Any> String.execAndMap(transform: (ResultSet) -> T): List<T> {
    val result = arrayListOf<T>()
    TransactionManager.current().exec(this) { rs ->
        while (rs.next()) {
            result += transform(rs)
        }
    }
    return result
}

fun dbExists(): Boolean = try {
    transaction { "SHOW TABLES LIKE 'Coin';".execAndMap { }.isNotEmpty() }
} catch (ex: Exception) {
    false
}

fun createDB() {
    println("Generating tables...")
    transaction {
        /*
        GENERATORS
         */
        //language=MySQL
        """CREATE TABLE Generator(
           id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
           name VARCHAR(20) NOT NULL,
           icon VARCHAR(60) NOT NULL,
           rate NUMERIC NOT NULL
        );""".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Generator_id_uindex ON cryptoclicker.Generator (id);".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Generator_name_uindex ON cryptoclicker.Generator (name);".execAndMap { }
        /*
        Upgrades
         */
        //language=MySQL
        """CREATE TABLE Upgrade
        (
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            name VARCHAR(20) NOT NULL,
            icon VARCHAR(60) NOT NULL
        );""".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrade_id_uindex ON cryptoclicker.Upgrade (id);".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrade_name_uindex ON cryptoclicker.Upgrade (name);".execAndMap { }
        /*
        UserState
         */
        //language=MySQL
        """CREATE TABLE UserState
        (
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            balance NUMERIC NOT NULL,
            lastSimulation BIGINT NOT NULL
        );""".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX UserState_id_uindex ON cryptoclicker.UserState (id);".execAndMap { }
        /*
        GENERATORS
         */
        //language=MySQL
        """CREATE TABLE Generators
        (
            generator_id INT,
            state_id INT,
            CONSTRAINT Generators_generator_id_state_id_pk PRIMARY KEY (generator_id, state_id),
            CONSTRAINT Generators_Generator_id_fk FOREIGN KEY (generator_id) REFERENCES cryptoclicker.Generator (id),
            CONSTRAINT Generators_UserState_id_fk FOREIGN KEY (state_id) REFERENCES cryptoclicker.UserState (id)
        );""".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Generators_state_id_uindex ON cryptoclicker.Generators (state_id);".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Generators_generator_id_uindex ON cryptoclicker.Generators (generator_id);".execAndMap { }
        /*
        UPGRADES
         */
        //language=MySQL
        """CREATE TABLE Upgrades
        (
            upgrade_id INT,
            state_id INT,
            CONSTRAINT Upgrades_upgrade_id_state_id_pk PRIMARY KEY (upgrade_id, state_id),
            CONSTRAINT Upgrades_Upgrade_id_fk FOREIGN KEY (upgrade_id) REFERENCES cryptoclicker.Upgrade (id),
            CONSTRAINT Upgrades_UserState_id_fk FOREIGN KEY (state_id) REFERENCES cryptoclicker.UserState (id)
        );""".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrades_state_id_uindex ON cryptoclicker.Upgrades (state_id);".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Upgrades_upgrade_id_uindex ON cryptoclicker.Upgrades (upgrade_id);".execAndMap { }
        /*
        COIN
         */
        //language=MySQL
        """CREATE TABLE Coin
        (
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            name VARCHAR(20) NOT NULL,
            icon VARCHAR(60) NOT NULL
        );""".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Coin_id_uindex ON cryptoclicker.Coin (id);".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX Coin_name_uindex ON cryptoclicker.Coin (name);".execAndMap { }
        /*
        USER
         */
        //language=MySQL
        """CREATE TABLE User
        (
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
        name VARCHAR(20) NOT NULL,
        password VARCHAR(60) NOT NULL,
        email VARCHAR(60) NOT NULL,
        state_id INT NOT NULL,
        coin_id INT NOT NULL,
        CONSTRAINT User_Coin_id_fk FOREIGN KEY (coin_id) REFERENCES cryptoclicker.Coin (id),
        CONSTRAINT User_UserState_id_fk FOREIGN KEY (state_id) REFERENCES cryptoclicker.UserState (id)
        );""".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX User_id_uindex ON cryptoclicker.User (id);".execAndMap { }
        //language=MySQL
        "CREATE UNIQUE INDEX User_name_uindex ON cryptoclicker.User (name);".execAndMap { }
    }
}