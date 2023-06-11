package barril.ide.view

import barril.ide.ConfigManager
import barril.ide.ResourcesUtil.getIcon
import barril.ide.exception.ConfigNaoInicializadaException
import java.awt.*
import java.awt.event.*
import java.io.File
import javax.swing.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class FramePrincipal(tamanho: Dimension) : JFrame("SimpleIDE") {
    private var pastaAberta = System.getProperty("user.home")

    private var explorador: ExploradorDeArquivosPanel
    private var editor: EditorDeTextoComAbasPanel
    private var splitPane: JSplitPane

    init {
        if(!ConfigManager.inicializado)
            throw ConfigNaoInicializadaException()

        this.contentPane.background = Color(45,45,55)

        layout = GridLayout()

        size = tamanho
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) = fecharProgramaSeConfirmar()
        })
        contentPane.background = Color(45, 45, 55)
        iconImage = getIcon("bskMiniatura.png").image
        jMenuBar = MenuBarPrincipal(this)

        val tamanhoExplorador = Dimension((tamanho.width * 0.2).roundToInt(), tamanho.height - jMenuBar.height)
        val tamanhoEditor = Dimension((tamanho.width * 0.8).roundToInt(), tamanho.height - jMenuBar.height)

        explorador = ExploradorDeArquivosPanel(pastaAberta, tamanhoExplorador)
        editor = EditorDeTextoComAbasPanel(tamanhoEditor)

        explorador.adicionarArquivoSelecionadoListener {
            editor.abrirArquivo(it)
        }

        splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.explorador, this.editor)
        splitPane.background = Color(45,45,55)
        add(splitPane)
        pack()
    }

    fun selecionouArquivoRecenteParaAbrir(nomeDoArquivoSelecionado: String) {
        editor.abrirArquivo(nomeDoArquivoSelecionado)
    }

    fun clicouBotaoSair(e: ActionEvent?) {
        fecharProgramaSeConfirmar()
    }

    fun fecharProgramaSeConfirmar() {
        val resultado = JOptionPane.showConfirmDialog(this, "Você realmente deseja sair? Mudanças não salvas serão perdidas.", "Sair", JOptionPane.YES_NO_CANCEL_OPTION)
        if(resultado == JOptionPane.YES_OPTION) {
            ConfigManager.salvar()
            exitProcess(0)
        }
    }

    fun clicouBotaoAbrirPasta(e: ActionEvent) {
        val fileChooser = JFileChooser(pastaAberta)
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        fileChooser.dialogType = JFileChooser.OPEN_DIALOG

        val resultado = fileChooser.showOpenDialog(this)
        if (resultado == JFileChooser.APPROVE_OPTION) {
            val f = File(fileChooser.selectedFile.absolutePath)

            val tamanhoExplorador = Dimension(explorador.size.width, explorador.size.height)
            splitPane.remove(explorador)
            explorador = ExploradorDeArquivosPanel(f, tamanhoExplorador)
            splitPane.add(explorador)
            pastaAberta = f.absolutePath
        }
    }

    fun clicouBotaoAbrir(e: ActionEvent) {
        val fileChooser = JFileChooser(pastaAberta)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        fileChooser.dialogType = JFileChooser.OPEN_DIALOG

        val resultado = fileChooser.showOpenDialog(this)
        if (resultado == JFileChooser.APPROVE_OPTION) {
            val f = File(fileChooser.selectedFile.absolutePath)
            editor.abrirArquivo(f)
        }
    }

    fun clicouBotaoSalvar(e: ActionEvent) {
        val caminho = editor.caminhoDoArquivoAberto
        if (caminho != null) {
            editor.salvarArquivo(caminho)
        } else {
            clicouBotaoSalvarComo(null)
        }
        explorador.recarregarArvore()
    }

    fun clicouBotaoSalvarComo(e: ActionEvent?) {
        val fileChooser = JFileChooser(pastaAberta)
        fileChooser.dialogType = JFileChooser.SAVE_DIALOG
        if(editor.caminhoDoArquivoAberto != null) {
            fileChooser.selectedFile = File(editor.caminhoDoArquivoAberto)
        } else if(editor.nomeDoArquivoAberto != null){
            fileChooser.selectedFile = File(editor.nomeDoArquivoAberto)
        }
        val resultado = fileChooser.showSaveDialog(this)
        if(resultado == JFileChooser.APPROVE_OPTION) {
            editor.salvarArquivo(File(fileChooser.selectedFile.absolutePath))
            explorador.recarregarArvore()
        }
    }

    fun clicouBotaoAbrirConfiguracoes(e: ActionEvent) {
        val configDialog = ConfigDialog(this, Dimension(this.size.width /4, this.size.height /4)) {
            editor.atualizarTamanhoTexto()
        }
        configDialog.abrir()
    }

    fun clicouRecarregarExplorador(e: ActionEvent?) {
        explorador.recarregarArvore()
    }

    fun clicouCriarArquivoVazio(actionEvent: ActionEvent?) {
        editor.criarAbaVaziaComPopup()
    }
}

// Código para adicionar a funcionalidade de menu de contexto no explorador.
// TODO
//    private fun configurarMenuDeContexto() {
//        val popupMenu = JPopupMenu().apply {
//        }
//        val listener = object : MouseAdapter() {
//            override fun mouseClicked(e: MouseEvent?) {
//                e ?: throw NullPointerException()
//
//                if (SwingUtilities.isRightMouseButton(e)) {
////                    val row = arvore.getClosestRowForLocation(e.x, e.y);
////                    arvore.setSelectionRow(row);
//                    popupMenu.show(e.component, e.x, e.y)
//                }
//            }
//        }
//        explorador.addMouseListener(listener)
//    }

    // FIXME
//    private fun mudarTema(tema: TemaIDE) {
//        when(tema) {
//            TemaIDE.CLARO -> {
//                this.contentPane.background = Color(175, 175, 190)
//                this.contentPane.background = Color(75, 75, 85)
//            }
//            TemaIDE.ESCURO -> {
//                this.contentPane.background = Color(75, 75, 85)
//                this.contentPane.background = Color(75, 75, 85)
//            }
//        }
//    }
//enum class TemaIDE {
//    CLARO, ESCURO
//}
