import spark.Spark.*
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

    get("/hello") { req, res -> "Hello World" }
}