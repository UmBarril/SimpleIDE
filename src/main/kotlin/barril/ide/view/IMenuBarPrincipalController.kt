package barril.ide.view

import java.awt.event.ActionEvent
import java.awt.event.MouseEvent

interface IMenuBarPrincipalController {
    fun clicouBotaoAbrirPasta(e: ActionEvent)

    fun clicouBotaoAbrir(e: ActionEvent)

    fun clicouBotaoSalvar(e: ActionEvent)

    fun clicouBotaoSalvarComo(e: ActionEvent?)

    fun clicouBotaoAbrirConfiguracoes(e: ActionEvent)

    fun selecionouArquivoRecenteParaAbrir(nomeDoArquivoSelecionado: String)

    fun clicouBotaoSair(actionEvent: ActionEvent?)
}