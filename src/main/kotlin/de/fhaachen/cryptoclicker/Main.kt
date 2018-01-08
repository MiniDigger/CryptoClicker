package de.fhaachen.cryptoclicker

import spark.Route
import spark.Spark.get
import spark.Spark.staticFiles

val localhost = true

fun main(args: Array<String>) {
    if (localhost) {
        val projectDir = System.getProperty("user.dir")
        val staticDir = "/src/de.fhaachen.cryptoclicker.main/resources/web"
        staticFiles.externalLocation(projectDir + staticDir)
    } else {
        staticFiles.location("/web")
    }

    val db = DB()
    val userHandler = UserHandler(db)
    val sim = Simulation(userHandler)

    get("/user/:name", Route({ req, res ->
        userHandler.getUserByName(req.params("name"))
    }), JsonTransformer())

    get("/state/:name/:recalc", Route({ req, res ->
        sim.status(req.params("name"), req.params("recalc"))
    }), JsonTransformer())

    get("/login/:email/:pw", Route({ req, res ->
        val email = req.params("email")
        val pw = req.params("pw")
        userHandler.login(email, pw)
    }), JsonTransformer())

    get("/register/:email/:pw/:name/:coin", Route({ req, res ->
        userHandler.createUser(
                req.params("name"),
                req.params("email"),
                req.params("pw"),
                req.params("coin")
        )
    }), JsonTransformer())
}