package IDE

import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.text.DefaultEditorKit
import javax.swing.text.StyleConstants

/**
 * Classe que segura os editores de texto
 * @param dimensao tamanho do componente
 */
class EditorDeTextoComAbas(dimensao: Dimension) : JPanel(GridLayout()) {
    private val tabbedPane = JTabbedPane()
    val arquivoAberto: String?
        get() = (tabbedPane.selectedComponent as? EditorDeTexto)?.caminhoOriginal

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

    /**
     * Abrir o arquivo pelo seu caminho.
     */
    fun abrirAquivo(caminho: String) = abrirAquivo(File(caminho))

    /**
     * Abrir o arquivo pelo objeto do arquivo.
     */
    fun abrirAquivo(arquivo: File) {
        if (arquivo.isFile) {
            // TODO: Melhorar otimização desta parte do código
            val linhas = arquivo.readLines() // procurar uma função de ler linhas mais otimizada

            tabbedPane.add(arquivo.name, EditorDeTexto(linhas.joinToString("\n"), arquivo.absolutePath))
            tabbedPane.selectedIndex = tabbedPane.tabCount - 1
        }
    }

    fun salvarArquivo(caminho: String) {
        salvarArquivo(File(caminho))
    }

    fun salvarArquivo(arquivo: File) {
        arquivo.writeText((tabbedPane.selectedComponent as EditorDeTexto).conteudo)
    }

    /**
     * Classe para editar/visualizar o conteúdo de um arquivo.
     * @param conteudo
     */
    class EditorDeTexto(var conteudo: String = "", val caminhoOriginal: String? = null) : JPanel() {
        init {
            layout = GridLayout(1,1)

            val textPane = JTextPane()
            val scrollPane = JScrollPane()

            textPane.actionMap.get(DefaultEditorKit.beepAction).isEnabled = false // desabilitar sons de beep
            textPane.font = Font("Arial", Font.PLAIN, 20)
            textPane.background = Color(45, 45, 55)
            textPane.foreground = Color.WHITE
            textPane.caretColor = Color.WHITE

            val doc = textPane.styledDocument
            val style = textPane.addStyle("", null) //?????
            StyleConstants.setForeground(style, Color.WHITE)
            StyleConstants.setBackground(style, Color.LIGHT_GRAY)

            doc.insertString(doc.length, conteudo, style)

            scrollPane.add(textPane)
            add(textPane)
            // scrollPane.rowHeader
        }

        override fun paintComponent(g: Graphics?) {
            super.paintComponent(g)

            // TODO: Adicionar botão de fechar aba
        }

        // TODO: Funcionalidade de detectar linguagem e adicionar highlights
        //val types = arrayOf("i128", "i64", "i32", "i16")

//        private fun mudarTamanhoTexto(tamanho: Float) {
//            textPane.font = textPane.font.deriveFont(Font.PLAIN, tamanho)
//        }
    }
}