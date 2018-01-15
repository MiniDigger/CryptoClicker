package de.fhaachen.cryptoclicker

import spark.Route
import spark.Spark.*

val localhost = true

fun main(args: Array<String>) {
    if (localhost) {
        val projectDir = System.getProperty("user.dir")
        val staticDir = "/src/de.fhaachen.cryptoclicker.main/resources/web"
        staticFiles.externalLocation(projectDir + staticDir)
    } else {
        staticFiles.location("/web")
    }

    val db = DB
    val userHandler = UserHandler(db)
    val sim = Simulation(userHandler)

    get("/user/:name", Route({ req, res ->
        userHandler.getOrLoadUser(name2 = req.params("name"))
    }), JsonTransformer())

    get("/state/:name/:recalc", Route({ req, res ->
        sim.status(req.params("name"), req.params("recalc"))
    }), JsonTransformer())

    post("/login/", Route({ req, res ->
        val email = req.queryParams("email")
        val pw = req.queryParams("pw")
        userHandler.login(email, pw)
    }), JsonTransformer())

    post("/register/", Route({ req, res ->
        userHandler.createUser(
                req.queryParams("name"),
                req.queryParams("email"),
                req.queryParams("pw"),
                req.queryParams("coin")
        )
    }), JsonTransformer())
}
