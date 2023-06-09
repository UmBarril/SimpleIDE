package IDE

import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.DefaultEditorKit
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

/**
 * Classe que segura os editores de texto
 * @param dimensao tamanho do componente
 */
class EditorDeTextoComAbas(dimensao: Dimension) : JPanel(GridLayout()) {

    private val tabbedPane = JTabbedPane()
    val arquivoAberto: String?
        get() = (tabbedPane.selectedComponent as? EditorDeTexto)?.caminhoDoArquivo

    fun abrirArquivo(arquivo: File) {
        for(i in 0 until tabbedPane.tabCount) {
            val component = tabbedPane.getComponentAt(i)
            if(component is EditorDeTexto) {
                if(component.caminhoDoArquivo == arquivo.path || component.caminhoDoArquivo == arquivo.absolutePath) {
                    tabbedPane.selectedIndex = i
                    return
                }
            }
        }
        if(ConfigManager["arquivosPossiveisDeAbrir"].split(';').contains(arquivo.extension)) {
            val opcoes = arrayOf("Apenas essa vez.", "Sempre abrir este tipo de arquivo.", "Cancelar.")
            val resultado = JOptionPane.showOptionDialog(this,
                "A extensão de arquivo '${arquivo.extension}' não está na nossa lista de arquivos suportados. \n Deseja tentar abrir mesmo assim? (pode travar o programa)",
                "Extensão de arquivo desconhecida.",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0])

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

    init {
        preferredSize = dimensao
        layout = GridLayout(1,1)

        val painelInicial = JPanel()
        painelInicial.add(JLabel("Nenhum arquivo aberto!\n Vá em Arquivo > Abrir para começar, ou clique no botão a seguir!"))
        val botao = JButton("Criar um novo arquivo.")
        botao.addActionListener { // TODO corrigir que isso não está indo para o histórico de arquivos abertos
            criarArquivoVazio(JOptionPane.showInputDialog("Digite o nome do novo arquivo: "))
            tabbedPane.remove(painelInicial)
        }
        painelInicial.add(botao)

        tabbedPane.addTab("Início", painelInicial)
        add(tabbedPane)
    }

    fun criarArquivoVazio(nome: String) {
        tabbedPane.addTab(nome, EditorDeTexto(""))
    }

    fun salvarArquivo(caminho: String) {
        salvarArquivo(File(caminho))
    }

    fun salvarArquivo(arquivo: File) {
        arquivo.writeText((tabbedPane.selectedComponent as EditorDeTexto).conteudo)
    }

    private fun abrirAquivoSemVerificacao(arquivo: File) {
        if (arquivo.isFile) {

            // TODO: Melhorar otimização desta parte do código
            val linhas = arquivo.readLines() // procurar uma função de ler linhas mais otimizada

            tabbedPane.add(arquivo.name, EditorDeTexto(linhas.joinToString("\n"), arquivo.absolutePath))
            tabbedPane.selectedIndex = tabbedPane.tabCount - 1
        }
    }

    /**
     * Classe para editar/visualizar o conteúdo de um arquivo.
     * @param conteudo
     * @param caminhoDoArquivo Caminho do arquivo aberto se houver.
     * @param apenasLeitura Caso true, o conteúdo não poderá ser editado. Padrão = false
     */
    class EditorDeTexto(var conteudo: String = "", val caminhoDoArquivo: String? = null, var apenasLeitura: Boolean = false): JPanel() {
        private val areaDeEscrita: JTextPane
        private val contadorLinhas: JTextPane
        private val scrollPane: JScrollPane

        init {
            this@EditorDeTexto.size = Dimension(600, 600)
            this@EditorDeTexto.layout = GridLayout(1,1)
            this@EditorDeTexto.background = Color(45,45,55)

            areaDeEscrita = JTextPane().apply escrita@{
                this@escrita.preferredSize = this@EditorDeTexto.preferredSize
                this@escrita.background = Color(45,45,55) // cor de fundo do painel de texto.
                this@escrita.foreground = Color.WHITE // cor das letras.
                this@escrita.font = Font("Arial", Font.PLAIN, 20) // fonte do painel de texto.
                this@escrita.actionMap.get(DefaultEditorKit.beepAction).isEnabled = false // desabilitar sons de beep.
                this.caretColor = Color.WHITE // cor do cursos piscante.
                this.isEditable = !apenasLeitura // definir se você pode ou não escrever no arquivo.

                val doc = this.styledDocument
                val style = this.addStyle("", null) //?????
                StyleConstants.setForeground(style, Color.WHITE)
                doc.insertString(doc.length, conteudo, style)
            }

            contadorLinhas = JTextPane().apply linhas@{
                this@linhas.preferredSize = Dimension(this@EditorDeTexto.width/12, this@EditorDeTexto.height) // tamanho preferível do contador de linhas.
                this@linhas.background = Color(35,35,45) // cor de fundo do contador de linhas.
                this@linhas.foreground = Color.WHITE // cor dos números das linhas.
                this.isEditable = false // nega a permissão de escrever no contador de linhas.

                val alinharDireita = SimpleAttributeSet()
                StyleConstants.setAlignment(alinharDireita, StyleConstants.ALIGN_RIGHT)

                this.setParagraphAttributes(alinharDireita, true) // atributo de parágrafo do contador de linhas
            }
            contarLinhas()

                areaDeEscrita.styledDocument.addDocumentListener(object : DocumentListener {
                    override fun insertUpdate(e: DocumentEvent?) {
                        contarLinhas()
                    }

                    override fun removeUpdate(e: DocumentEvent?) {
                        contarLinhas()
                    }

                    override fun changedUpdate(e: DocumentEvent?) {
                        contarLinhas()
                    }
                } )

            scrollPane = JScrollPane(areaDeEscrita).apply scroll@{
                this@scroll.preferredSize = this@EditorDeTexto.size
            }
            scrollPane.setRowHeaderView(contadorLinhas)

            this@EditorDeTexto.add(scrollPane)
        }

        private fun contarLinhas() {
            val doc = contadorLinhas.styledDocument
            val docStyle = SimpleAttributeSet()
            StyleConstants.setForeground(docStyle, Color.WHITE)
            StyleConstants.setFontSize(docStyle, 20)
            val conteudo = areaDeEscrita.text

            for (linha in 0 until conteudo.split("\n").size) {
                doc.insertString(doc.length, "${linha+1}\n", docStyle)
            }

        }

    }
}