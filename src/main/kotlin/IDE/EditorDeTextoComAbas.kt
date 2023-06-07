package IDE

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.io.File
import javax.swing.*
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

        // TODO: Talvez colocar um botão para criar o arquivo diretamente por aqui?
        tabbedPane.addTab("Início", JLabel("Nenhum arquivo aberto!\n Vá em Arquivo > Abrir para começar!"))
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
            val linhas = arquivo.readLines().toString() // procurar uma função de ler linhas mais optimizada
            tabbedPane.add(arquivo.name, EditorDeTexto(linhas))
        }
    }

    fun salvarArquivo(caminho: String) {
        salvarArquivo(File(caminho))
    }

    fun salvarArquivo(arquivo: File) {
        throw Exception("não implementado")
        // TODO
    }

    /**
     * Classe para editar/visualizar o conteúdo de um arquivo.
     * @param conteudo
     * @param apenasLeitura caso true, o conteudo não podera ser editado. Padrão = false
     */
    class EditorDeTexto(var conteudo: String = "", val caminhoOriginal: String? = null, val apenasLeitura: Boolean = false) : JPanel() {
        init {
            layout = GridLayout(1,1)

            val textPane = JTextPane()
            val scrollPane = JScrollPane()

            textPane.font = Font("Arial", Font.PLAIN, 20)
            textPane.background = Color(45, 45, 55)

            val doc = textPane.styledDocument
            val style = textPane.addStyle(conteudo, null)
            StyleConstants.setForeground(style, Color.WHITE)
            StyleConstants.setBackground(style, Color.LIGHT_GRAY)

            doc.insertString(doc.length, "texto", style)

            scrollPane.add(textPane)
            add(scrollPane)
            // scrollPane.rowHeader
        }
        // TODO: Funcionalidade de detectar linguagem e adicionar highlights
        //val types = arrayOf("i128", "i64", "i32", "i16")

//        private fun mudarTamanhoTexto(tamanho: Float) {
//            textPane.font = textPane.font.deriveFont(Font.PLAIN, tamanho)
//        }
    }
}