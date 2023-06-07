package IDE

import java.io.File
import java.io.FileWriter
import java.util.Properties

class ConfigManager() {
    private val prop = Properties()
    private val arquivoConfig = File("config.properties")

    fun carregar() {
        val writer = FileWriter(arquivoConfig)
    }

    fun salvar() {
        val writer = FileWriter(arquivoConfig)
    }

    operator fun get(chave: String) = prop.getProperty(chave)
    operator fun set(chave: String, valor: String) = prop.setProperty(chave, valor)
}