package IDE

import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.text.*

/**
 * Classe que segura os editores de texto
 * @param dimensao tamanho do componente
 */
class EditorDeTextoComAbas(dimensao: Dimension) : JPanel(GridLayout()) {

    private val tabbedPane = JTabbedPane()
    val arquivoAberto: String?
        get() = (tabbedPane.selectedComponent as? EditorDeTexto)?.caminhoDoArquivo

    init {
        preferredSize = dimensao
        layout = GridLayout(1,1)
        foreground = Color.WHITE

        abrirAbaESelecionar("Início", criarTelaInicial())
        add(tabbedPane.apply {
            foreground = Color(100,50,140)
        })
    }

    fun abrirArquivo(arquivo: File) {
        // adicionando arquivo aos arquivos recente
        if(ConfigManager["arquivosRecentes"].isEmpty()) {
            ConfigManager["arquivosRecentes"] = arquivo.path
        } else {
            val cincoMaisRecentes: List<String> = ConfigManager["arquivosRecentes"].split(';', limit=5)
            ConfigManager["arquivosRecentes"] = arquivo.path + ";" + cincoMaisRecentes.joinToString()
        }

        // verificando se o arquivo não já está aberto em outro lugar
        for(i in 0 until tabbedPane.tabCount) {
            val component = tabbedPane.getComponentAt(i)
            if(component is EditorDeTexto) {
                if(component.caminhoDoArquivo == arquivo.path || component.caminhoDoArquivo == arquivo.absolutePath) {
                    tabbedPane.selectedIndex = i
                    return
                }
            }
        }

        // Conferindo se esta tetnando abrir um arquivo não suportado
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

    private fun criarTelaInicial(): JPanel {
        val painelInicial = JPanel().apply painelInicial@{
            this@painelInicial.background = Color(45,45,55)
            this@painelInicial.foreground = Color.WHITE
        }
        painelInicial.add(JLabel("Nenhum arquivo aberto!\n Vá em Arquivo > Abrir para começar, ou clique no botão a seguir!").apply {
            foreground = Color.WHITE
        })
        val botao = JButton("Criar um novo arquivo.").apply botao@{
            this@botao.background = Color(100,50,140)
            this@botao.foreground = Color.WHITE
        }
        botao.addActionListener { // TODO corrigir que isso não está indo para o histórico de arquivos abertos
            criarArquivoVazio(JOptionPane.showInputDialog("Digite o nome do novo arquivo: ").apply {
                background = Color(45,45,55)
                foreground = Color.WHITE
            })
            tabbedPane.removeTabAt(0)
        }
        painelInicial.add(botao)

        return painelInicial
    }

    private fun criarArquivoVazio(nome: String) = abrirAbaESelecionar(nome, EditorDeTexto())

    fun salvarArquivo(caminho: String) = salvarArquivo(File(caminho))

    fun salvarArquivo(arquivo: File) {
        val tab = tabbedPane.getTabComponentAt(tabbedPane.selectedIndex)
        if(tab is EditorDeTexto){
            if(tab.caminhoDoArquivo?.isNotEmpty() == true){
                arquivo.writeText((tabbedPane.selectedComponent as EditorDeTexto).conteudo)
            } else {
                JOptionPane.showMessageDialog(null, "Abra um arquivo primeiro!")
            }
        }
    }

    private fun abrirAquivoSemVerificacao(arquivo: File) {
        if (arquivo.isFile) {
            // TODO: Melhorar otimização desta parte do código (talvez fazer uma stream de texto)
            val linhas = arquivo.readLines()
            abrirAbaESelecionar(arquivo.name, EditorDeTexto(linhas.joinToString("\n"), arquivo.absolutePath))
        }
    }

    // editor deve ser JPanel. Por alguma razão algumas coisas não funcionando se não for.
    // Por isso esse overload
    private fun abrirAbaESelecionar(nomeDaAba: String, editor: EditorDeTexto) {
        val panel = JPanel(GridLayout(1,1))
        panel.add(editor)
        abrirAbaESelecionar(nomeDaAba, panel)
    }

    private fun abrirAbaESelecionar(nomeDaAba: String, c: JPanel) {
//        val frame = JFrame()
//        frame.add(c)
//        frame.isVisible = true
        tabbedPane.add(nomeDaAba, c.apply {
            background = Color(45,45,55)
            foreground = Color.WHITE
        })
        tabbedPane.selectedIndex = tabbedPane.tabCount - 1
    }

    internal class ScrollablePanel : JPanel(), Scrollable {
        override fun getPreferredScrollableViewportSize(): Dimension {
            //the panel prefers to take as much height as possible
            return Dimension(preferredSize.width, Int.MAX_VALUE)
        }

        override fun getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int {
            return 1
        }

        override fun getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int {
            return 1
        }

        override fun getScrollableTracksViewportWidth(): Boolean {
            return true
        }

        override fun getScrollableTracksViewportHeight(): Boolean {
            return true
        }
    }

    /**
     * Classe para editar/visualizar o conteúdo de um arquivo.
     * @param conteudo
     * @param caminhoDoArquivo Caminho do arquivo aberto se houver.
     * @param apenasLeitura Caso true, o conteúdo não poderá ser editado. Padrão = false
     */
    class EditorDeTexto(
        var conteudo: String = "",
        val caminhoDoArquivo: String? = null,
        private var apenasLeitura: Boolean = false ,
        tamanho: Dimension = Dimension(600, 600)
    ) : JScrollPane() {
        private val areaDeEscrita: JTextArea
        private val contadorDeLinhas: JTextPane

        init {
            preferredSize = tamanho
            background = Color(45, 45, 55)

            areaDeEscrita = criarAreaDeEscrita()
            contadorDeLinhas = criarContadorDeLinhas()

            val panel = ScrollablePanel()
            panel.layout = GridBagLayout()

            //can be reused as constraints are only read when adding components
            val constraints = GridBagConstraints()
            //components should take all vertical space if possible.
            constraints.weighty = 1.0
            //components should expand even if it doesn't need more space
            constraints.fill = GridBagConstraints.BOTH
            //add the numbers component
            panel.add(contadorDeLinhas, constraints)

            //add specific constraints for the text component
            //text takes as much of the width as it can get
            constraints.weightx = 1.0

            //numbers component seems to have some insets so add 2px at the top to get better alignment - could be done differently as well
            constraints.insets = Insets(2, 0, 0, 0)
            panel.add(areaDeEscrita, constraints)

            verticalScrollBarPolicy = VERTICAL_SCROLLBAR_ALWAYS
            setViewportView(panel)

            atualizarContadorDeLinhas()
        }

        private fun criarAreaDeEscrita(): JTextArea {
            val areaDeEscrita = JTextArea()

//            areaDeEscrita.preferredSize = tamanhoPreferivel
            areaDeEscrita.background = Color(45, 45, 55) // cor de fundo do painel de texto.
            areaDeEscrita.foreground = Color.WHITE // cor das letras.
            areaDeEscrita.font = Font("Monospaced", Font.PLAIN, 20) // fonte do painel de texto.
            areaDeEscrita.actionMap.get(DefaultEditorKit.beepAction).isEnabled = false // desabilitar sons de beep. NAO FUNCIONA FIXME
            areaDeEscrita.border = EmptyBorder(0, 0, 0, 0)
            areaDeEscrita.caretColor = Color.WHITE // cor do cursos piscante.
            areaDeEscrita.isEditable = !apenasLeitura // definir se você pode ou não escrever no arquivo.

            val doc: Document = areaDeEscrita.document
            // Substituindo tabs por dois espaços
            (doc as AbstractDocument).documentFilter = object : DocumentFilter() {
                @Throws(BadLocationException::class)
                override fun replace(fb: FilterBypass?, offset: Int, length: Int, text: String, attrs: AttributeSet?) {
                    super.insertString(fb, offset, text.replace("\t", " "), attrs)
                }
            }

            val style = SimpleAttributeSet()
            StyleConstants.setForeground(style, Color.WHITE)
            doc.insertString(doc.length, conteudo, style)
            StyleConstants.setFontSize(style, 20)

            return areaDeEscrita
        }

        private fun criarContadorDeLinhas(): JTextPane {
            val contadorDeLinhas = JTextPane()

            contadorDeLinhas.font = Font("Monospaced", Font.PLAIN, 20) // fonte do painel de texto. 17
            contadorDeLinhas.border = EmptyBorder(0, 0, 0, 0)
            contadorDeLinhas.background = Color(35, 35, 45) // cor de fundo do contador de linhas.
            contadorDeLinhas.foreground = Color.WHITE // cor dos números das linhas.
            contadorDeLinhas.isEditable = false // nega a permissão de escrever no contador de linhas.

            val alinharCentro = SimpleAttributeSet()
            StyleConstants.setAlignment(alinharCentro, StyleConstants.ALIGN_CENTER)
            contadorDeLinhas.setParagraphAttributes(alinharCentro, true) // atributo de parágrafo do contador de linhas

            return contadorDeLinhas
        }

        private fun atualizarContadorDeLinhas() {
            val docStyle = SimpleAttributeSet()
            StyleConstants.setForeground(docStyle, Color.WHITE)
            StyleConstants.setFontFamily(docStyle, "Monospaced")
            StyleConstants.setFontSize(docStyle, 20)
        }

    }
}