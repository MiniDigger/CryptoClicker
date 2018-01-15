package de.fhaachen.cryptoclicker

val GPU = Generator(0, "de.fhaachen.cryptoclicker.getGPU", "gpu.png", 1.0)
val MAINFRAME = Generator(1, "Mainframe", "mainframe.png", 1.5)

private val generators: Map<String, Generator> = mapOf(GPU.name to GPU)

fun getGenerator(name: String): Generator? = generators[name]