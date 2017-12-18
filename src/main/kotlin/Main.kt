import spark.Spark.get
import spark.Spark.staticFiles

val localhost = true

fun main(args: Array<String>) {
    if (localhost) {
        val projectDir = System.getProperty("user.dir")
        val staticDir = "/src/main/resources/web"
        staticFiles.externalLocation(projectDir + staticDir)
    } else {
        staticFiles.location("/web")
    }

    val userHandler = UserHandler()

    get("/hello") { req, res -> "Hello World" }

    get("/user/:name") { req, res ->
        userHandler.getUserByName(req.params("name"))
    }

    get("/login/:email/:pw") { req, res ->
        val email = req.params("email")
        val pw = req.params("pw")
        userHandler.login(email, pw)
    }

    get("/register/:email/:pw/:name/:coin") { req, res ->
        userHandler.createUser(
                req.params("name"),
                req.params("email"),
                req.params("pw"),
                req.params("coin")
        )
    }
}