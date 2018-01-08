package de.fhaachen.cryptoclicker

import com.google.gson.GsonBuilder
import spark.ResponseTransformer

class JsonTransformer : ResponseTransformer {

    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    override fun render(model: Any): String {
        return gson.toJson(model)
    }
}