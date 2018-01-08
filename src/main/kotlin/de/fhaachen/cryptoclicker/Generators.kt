package de.fhaachen.cryptoclicker

import java.io.File

val GPU = Generator(0, "de.fhaachen.cryptoclicker.getGPU", File("gpu.png"), 1.0)
val MAINFRAME = Generator(1, "Mainframe", File("mainframe.png"), 1.5)

private val generators: Map<String, Generator> = mapOf(GPU.name to GPU)

fun getGenerator(name: String): Generator? = generators[name]