import spark.Spark.*
import spark.Spark.staticFiles
import java.util.*

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

    get("/user/:id") { req, res ->
        userHandler.getUser(UUID.fromString(req.params("id")))
    }
}