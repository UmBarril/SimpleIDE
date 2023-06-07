package IDE

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import javax.swing.*
import javax.swing.text.StyleConstants

class EditorDeTexto(caminhoDoArquivo: String, apenasLeitura: Boolean, dimensao: Dimension) : JPanel(GridLayout()) {
    val textPane = JTextPane()
    val scrollPane = JScrollPane(textPane)
    val types = arrayOf("i28", "i64", "i32", "i16")

    init {
//      UIManager.getLookAndFeel()

        val tabbedPane = JTabbedPane()

        textPane.font = Font("Arial", Font.PLAIN, 20)
        textPane.preferredSize = dimensao
        textPane.background = Color(45, 45, 55)

        val doc = textPane.styledDocument
        val style = textPane.addStyle("Estilo", null)
        StyleConstants.setForeground(style, Color.WHITE)
        StyleConstants.setBackground(style, Color.LIGHT_GRAY)

        doc.insertString(doc.length, "texto", style)

        add(scrollPane)
//        scrollPane.rowHeader
        // TODO
    }

    private fun mudarTamanhoTexto(tamanho: Float) {
        textPane.font = textPane.font.deriveFont(Font.PLAIN, tamanho)
    }

    class 
}