package barril.ide

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Properties

object ConfigManager {
    private val props = Properties()
    private val arquivoConfig = File("config.properties")

    var inicializado = false
        private set

    fun inicializar() {
        inicializado = true
        if(arquivoConfig.exists()) {
            props.load(FileReader(arquivoConfig))
        }
        else {
            println("Não foi encontrado um arquivo de configuração em (primeira vez utilizando o programa?): ${arquivoConfig.absolutePath}")
            println("Utilizando configurações padrão...")

            props.setProperty("editorTamanhoFonte", "20")
            props.setProperty("arquivosRecentes", "")
            val arquivosSuportadosNativamente = arrayOf(
                "java", "c", "txt", "py", "kt", "kts", "md", "html", "css", "js", "php", "json", "cpp", "pl", "cs", "bat", "ps1"
            )
            props.setProperty("arquivosPossiveisDeAbrir", arquivosSuportadosNativamente.joinToString(";"))
        }
    }

    fun salvar() {
        props.store(FileWriter(arquivoConfig), "Configurações gerais da SimpleIDE")
    }

    operator fun get(chave: String): String = props.getProperty(chave) ?: ""
    operator fun set(chave: String, valor: String): Any? = props.setProperty(chave, valor)
}