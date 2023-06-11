package barril.ide.view

import barril.ide.ConfigManager
import java.awt.Color
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.*

/**
 * Classe para editar/visualizar o conteúdo de um arquivo.
 * @param conteudoInicial
 * @param caminhoDoArquivo Caminho do arquivo aberto se houver.
 * @param apenasLeitura Caso true, o conteúdo não poderá ser editado. Padrão = false
 *
 * TODO: Tornar essa classe mais rápida
 */
class EditorDeTextoScroll(
    conteudoInicial: String = "",
    var caminhoDoArquivo: String? = null,
    private var apenasLeitura: Boolean = false,
) : JScrollPane() {
    private val areaDeEscrita: JTextPane
    private val contadorDeLinhas: JTextPane
    val conteudo: String
        get() = areaDeEscrita.document.getText(0, areaDeEscrita.document.length)

    val tamanhoDaFonte: Int
        get() = ConfigManager["editorTamanhoFonte"].toInt()

    fun atualizarTamanhoTexto() {
        areaDeEscrita.font = Font("Monospaced", Font.PLAIN, tamanhoDaFonte) // fonte do painel de texto.
        atualizarContadorDeLinhas()
    }

    fun getTextoSelecionado() : String? = areaDeEscrita.selectedText

    init {
        inheritsPopupMenu = true
        background = Color(45, 45, 55)

        areaDeEscrita = criarAreaDeEscrita(conteudoInicial)
        contadorDeLinhas = criarContadorDeLinhas()

        // https://stackoverflow.com/questions/69536141/jscrollpane-dynamic-rowheader-out-of-sync-when-resizing
        val panel = ScrollablePanel()
        panel.inheritsPopupMenu = true
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

        panel.add(areaDeEscrita, constraints)

        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_ALWAYS
        horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_ALWAYS

        verticalScrollBar.unitIncrement = 16

        setViewportView(panel)

        atualizarContadorDeLinhas()
        areaDeEscrita.styledDocument.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = atualizarContadorDeLinhas()
            override fun removeUpdate(e: DocumentEvent?) = atualizarContadorDeLinhas()
            override fun changedUpdate(e: DocumentEvent?) = atualizarContadorDeLinhas()
        })
    }

    private fun criarAreaDeEscrita(conteudoInicial: String): JTextPane {
        val areaDeEscrita = JTextPane()

        areaDeEscrita.inheritsPopupMenu = true
        areaDeEscrita.background = Color(45, 45, 55) // cor de fundo do painel de texto.
        areaDeEscrita.foreground = Color.WHITE // cor das letras.
        areaDeEscrita.font = Font("Monospaced", Font.PLAIN, tamanhoDaFonte) // fonte do painel de texto.
        areaDeEscrita.actionMap.get(DefaultEditorKit.beepAction).isEnabled = false // desabilitar sons de beep. NAO FUNCIONA FIXME
        areaDeEscrita.border = EmptyBorder(0, 0, 0, 0)
        areaDeEscrita.caretColor = Color.WHITE // cor do cursos piscante.
        areaDeEscrita.isEditable = !apenasLeitura // definir se você pode ou não escrever no arquivo.

        val doc: Document = areaDeEscrita.document

        // TODO: fazer isso ser configurável
        // Substituindo tabs por dois espaços
        (doc as AbstractDocument).documentFilter = object : DocumentFilter() {
            @Throws(BadLocationException::class)
            override fun replace(fb: FilterBypass?, offset: Int, length: Int, text: String, attrs: AttributeSet?) {
                super.insertString(fb, offset, text.replace("\t", " "), attrs)
            }
        }

        val style = SimpleAttributeSet()
        StyleConstants.setForeground(style, Color.WHITE)
        doc.insertString(doc.length, conteudoInicial, style)
        StyleConstants.setFontSize(style, 20)

        return areaDeEscrita
    }

    private fun criarContadorDeLinhas(): JTextPane {
        val contadorDeLinhas = JTextPane()

        contadorDeLinhas.font = Font("Monospaced", Font.PLAIN, tamanhoDaFonte) // fonte do painel de texto. 17
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
        StyleConstants.setFontSize(docStyle, tamanhoDaFonte)

        // Removendo todo o texto do documento
        val doc = contadorDeLinhas.document
        doc.remove(0, doc.length)

        val contagemDeLinhas: Int = this.areaDeEscrita.document.defaultRootElement.elementCount

        // Adicionando numeros de linha
        for (i in 1 .. contagemDeLinhas)  {
            doc.insertString(doc.length, "$i\n", docStyle)
        }
    }

}
