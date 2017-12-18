import spark.Route
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

    initUserHandler()

    get("/user/:name", Route({ req, res ->
        getUserByName(req.params("name"))
    }), JsonTransformer())

    get("/state/:name/:recalc", Route({ req, res ->
        simulation(req.params("name"), req.params("recalc"))
    }), JsonTransformer())

    get("/login/:email/:pw", Route({ req, res ->
        val email = req.params("email")
        val pw = req.params("pw")
        login(email, pw)
    }), JsonTransformer())

    get("/register/:email/:pw/:name/:coin", Route({ req, res ->
        createUser(
                req.params("name"),
                req.params("email"),
                req.params("pw"),
                req.params("coin")
        )
    }), JsonTransformer())
}