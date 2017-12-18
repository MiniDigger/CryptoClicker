import java.io.File

val GPU = Generator("GPU", File("gpu.png"), 1.0)

private val generators: Map<String, Generator> = mapOf(GPU.name to GPU)


fun getGenerator(name: String): Generator? = generators[name]