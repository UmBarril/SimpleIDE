package IDE

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Properties

class ConfigManager() {
    private val props = Properties()
    private val arquivoConfig = File("config.properties")

    fun carregar() {
        if(arquivoConfig.exists()) {
            props.load(FileReader(arquivoConfig))
        }
        else {
            println("Não foi encontrado um arquivo de configuração em (primeira vez utilizando o programa?): ${arquivoConfig.absolutePath}")
            println("Utilizando configurações padrão...")

            props.setProperty("editorTamanhoFonte", "20")
        }
    }

    fun salvar() {
        props.store(FileWriter(arquivoConfig), "Configurações gerais da SimpleIDE")
    }

    operator fun get(chave: String)  = props.getProperty(chave)
    operator fun set(chave: String, valor: String) = props.setProperty(chave, valor)
}