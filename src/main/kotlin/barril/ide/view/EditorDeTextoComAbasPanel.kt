package barril.ide.view

import barril.ide.ConfigManager
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*

/**
 * Classe que segura os editores de texto
 * @param dimensao tamanho do componente
 */
class EditorDeTextoComAbasPanel(dimensao: Dimension) : JPanel(GridLayout()) {
    private val tabbedPane = JTabbedPane()
    private var removeuTelaInicial = false

    val quantidadeDeAbas: Int
        get() = tabbedPane.tabCount

    val caminhoDoArquivoAberto: String?
        get() = (tabbedPane.selectedComponent as? EditorDeTextoScroll)?.caminhoDoArquivo

    val nomeDoArquivoAberto: String?
        get() = tabbedPane.getTitleAt(tabbedPane.selectedIndex)

    init {
        preferredSize = dimensao
        layout = GridLayout(1,1)
        foreground = Color.WHITE

        val ui = tabbedPane.ui
        tabbedPane.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e != null) {
                    if(e.isAltDown) {
                        tabbedPane.removeTabAt(ui.tabForCoordinate(tabbedPane, e.x, e.y))
                        tabbedPane.repaint()
                    }
                }
            }
        })

        val popup = JPopupMenu()
        popup.add(JMenuItem("Copiar").apply {
            addActionListener {
                val textoSelecionado = (tabbedPane.selectedComponent as EditorDeTextoScroll).getTextoSelecionado()
                if(textoSelecionado != null) {
                    val stringSelection = StringSelection(textoSelecionado)
                    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                    clipboard.setContents(stringSelection, null)
                }
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                e ?: return
                if(e.isPopupTrigger) {
                    val textoSelecionado = (tabbedPane.selectedComponent as EditorDeTextoScroll).getTextoSelecionado()
                    if(!textoSelecionado.isNullOrEmpty()) {
                        popup.show(this@EditorDeTextoComAbasPanel, e.x, e.y)
                    }
                }
            }
        })
        componentPopupMenu = popup

        abrirAbaESelecionar("Inicio", "", criarTelaInicial())
        add(tabbedPane.apply {
            inheritsPopupMenu = true
            foreground = Color(100,50,140)
        })
    }

    fun abrirArquivo(arquivo: File) {
        // verificando se o arquivo não já está aberto em outro lugar
        for(i in 0 until tabbedPane.tabCount) {
            val component = tabbedPane.getComponentAt(i)
            if(component is EditorDeTextoScroll) {
                if(component.caminhoDoArquivo == arquivo.path || component.caminhoDoArquivo == arquivo.absolutePath) {
                    tabbedPane.selectedIndex = i
                    return
                }
            }
        }

        // Conferindo se esta tentando abrir um arquivo não suportado
        if(!ConfigManager["arquivosPossiveisDeAbrir"].split(';').contains(arquivo.extension)) {
            val opcoes = arrayOf("Apenas essa vez.", "Sempre abrir este tipo de arquivo.", "Cancelar.")
            val resultado = JOptionPane.showOptionDialog(this,
                "A extensão do arquivo '${arquivo.name}' não está na nossa lista de arquivos suportados. \n Deseja tentar abrir mesmo assim? (pode travar o programa)",
                "Extensão de arquivo desconhecida.",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[2])

            when(resultado) {
                JOptionPane.YES_OPTION -> {}
                JOptionPane.NO_OPTION -> arquivo.extension + ConfigManager["arquivosPossiveisDeAbrir"]
                JOptionPane.CANCEL_OPTION -> return
            }
        }
        abrirAquivoSemVerificacao(arquivo)
    }

    /**
     * Abrir o arquivo a partir do seu caminho no sistema.
     */
    fun abrirArquivo(caminho: String): Unit = abrirArquivo(File(caminho))

    fun criarTelaInicial(): JPanel {
        val painelInicial = JPanel()
        painelInicial.background = Color(45,45,55)
        painelInicial.foreground = Color.WHITE

        painelInicial.add(JLabel("Nenhum arquivo aberto!\n Vá em Arquivo > Abrir para começar, ou clique no botão a seguir!").apply {
            foreground = Color.WHITE
        })
        val botao = JButton("Criar um novo arquivo.")
        botao.background = Color(100,50,140)
        botao.foreground = Color.BLACK

        botao.addActionListener { // TODO corrigir que isso não está indo para o histórico de arquivos abertos
            criarAbaVaziaComPopup()
            tabbedPane.removeTabAt(0)
        }
        painelInicial.add(botao)

        return painelInicial
    }

    fun atualizarTamanhoTexto() {
        for(i in 0 until tabbedPane.tabCount) {
            val tab = tabbedPane.getComponentAt(i)
            if(tab is EditorDeTextoScroll) {
                tab.atualizarTamanhoTexto()
            }
        }
    }
    fun criarAbaVazia(nome: String) = abrirAbaESelecionar(nome, "", EditorDeTextoScroll())

    /***
     * Cria um popup que pergunta o nome do novo arquivo a ser criado
     */
    fun criarAbaVaziaComPopup() {
        criarAbaVazia(JOptionPane.showInputDialog("Digite o nome do novo arquivo: ").apply {
            background = Color(45,45,55)
            foreground = Color.WHITE
        })
    }

    fun salvarArquivo(caminho: String) = salvarArquivo(File(caminho))

    fun salvarArquivo(arquivo: File) {
        val tab = tabbedPane.selectedComponent
        if(tab is EditorDeTextoScroll){
            if(tab.caminhoDoArquivo.isNullOrEmpty() && arquivo.exists()) {
                val resultado = JOptionPane.showConfirmDialog(this, "Já existe um arquivo nesse caminho. Deseja substituí-lo?", "Arquivo já existe", JOptionPane.YES_NO_OPTION)
                if(resultado == JOptionPane.NO_OPTION)
                    return
            }
            arquivo.writeText((tabbedPane.selectedComponent as EditorDeTextoScroll).conteudo)
            JOptionPane.showMessageDialog(null, "Salvo em '${arquivo.path}'!")
            tab.caminhoDoArquivo = arquivo.path
        } else {
            JOptionPane.showMessageDialog(null, "Abra um arquivo primeiro!")
        }
    }

    private fun abrirAquivoSemVerificacao(arquivo: File) {
        if (arquivo.isFile) {
            // TODO: Melhorar otimização desta parte do código (talvez fazer uma stream de texto)
            val linhas = arquivo.readLines()
            abrirAbaESelecionar(arquivo.name, arquivo.path, EditorDeTextoScroll(linhas.joinToString("\n"), arquivo.absolutePath))
        }
    }

    private fun abrirAbaESelecionar(nomeDaAba: String, caminho: String, c: JComponent) {
        // adicionando arquivo aos arquivos recente
        if(ConfigManager["arquivosRecentes"].isNullOrEmpty()) {
            ConfigManager["arquivosRecentes"] = caminho
        } else {
            val cincoMaisRecentes: List<String> = ConfigManager["arquivosRecentes"].split(';', limit=5)
            ConfigManager["arquivosRecentes"] = caminho + ";" + cincoMaisRecentes.joinToString(";")
        }

        tabbedPane.add(nomeDaAba, c.apply {
            background = Color(45,45,55)
            foreground = Color.WHITE
        })
        tabbedPane.selectedIndex = tabbedPane.tabCount - 1
    }
}