package IDE

import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.*
import javax.swing.text.html.HTMLEditorKit

/**
 * Classe que segura os editores de texto
 * @param dimensao tamanho do componente
 */
class EditorDeTextoComAbas(dimensao: Dimension) : JPanel(GridLayout()) {

    private val tabbedPane = JTabbedPane()
    val arquivoAberto: String?
        get() = (tabbedPane.selectedComponent as? EditorDeTexto)?.caminhoDoArquivo

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
            tabbedPane.removeTabAt(0)
        }
        painelInicial.add(botao)

        tabbedPane.addTab("Início", painelInicial)
        add(tabbedPane)
    }

    private fun criarArquivoVazio(nome: String) {
        tabbedPane.addTab(nome, EditorDeTexto())
    }

    fun salvarArquivo(caminho: String) {
        salvarArquivo(File(caminho))
    }

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
    class EditorDeTexto(
        var conteudo: String = "",
        val caminhoDoArquivo: String? = null,
        private var apenasLeitura: Boolean = false ,
        tamanho: Dimension = Dimension(600, 600)
    ) : JPanel() {
        private val areaDeEscrita: JTextPane
        private val contadorDeLinhas: JTextPane

        init {
            size = tamanho
            layout = GridLayout(1, 1)
            background = Color(45, 45, 55)

            areaDeEscrita = criarAreaDeEscrita(preferredSize)
            contadorDeLinhas = criarContadorDeLinhas(Dimension(this.width / 12, this.height))

            val scrollPane = JScrollPane().apply {
                preferredSize = tamanho
                setRowHeaderView(contadorDeLinhas)
                setViewportView(areaDeEscrita)
            }
            atualizarContadorDeLinhas()
            areaDeEscrita.styledDocument.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent?) = atualizarContadorDeLinhas()
                override fun removeUpdate(e: DocumentEvent?) = atualizarContadorDeLinhas()
                override fun changedUpdate(e: DocumentEvent?) = atualizarContadorDeLinhas()
            })
            this.add(scrollPane)
        }

        private fun criarAreaDeEscrita(tamanhoPreferivel: Dimension): JTextPane {
            val areaDeEscrita = JTextPane()

            areaDeEscrita.preferredSize = tamanhoPreferivel
            areaDeEscrita.background = Color(45, 45, 55) // cor de fundo do painel de texto.
            areaDeEscrita.foreground = Color.WHITE // cor das letras.
            areaDeEscrita.font = Font("Arial", Font.PLAIN, 20) // fonte do painel de texto.
            areaDeEscrita.actionMap.get(DefaultEditorKit.beepAction).isEnabled = false // desabilitar sons de beep. NAO FUNCIONA FIXME
            areaDeEscrita.border = EmptyBorder(0, 0, 0, 0)
            areaDeEscrita.caretColor = Color.WHITE // cor do cursos piscante.
            areaDeEscrita.isEditable = !apenasLeitura // definir se você pode ou não escrever no arquivo.

            val doc = areaDeEscrita.styledDocument
            val style = SimpleAttributeSet()

            StyleConstants.setForeground(style, Color.WHITE)
            doc.insertString(doc.length, conteudo, style)
            StyleConstants.setFontSize(style, 20)

            return areaDeEscrita
        }

        private fun criarContadorDeLinhas(tamanhoPreferivel: Dimension): JTextPane {
            val contadorDeLinhas = JTextPane()

            contadorDeLinhas.preferredSize = tamanhoPreferivel
            contadorDeLinhas.font = Font("Arial", Font.PLAIN, 17) // fonte do painel de texto.
            contadorDeLinhas.border = EmptyBorder(0, 0, 0, 0)
            contadorDeLinhas.background = Color(35, 35, 45) // cor de fundo do contador de linhas.
            contadorDeLinhas.foreground = Color.WHITE // cor dos números das linhas.
            contadorDeLinhas.isEditable = false // nega a permissão de escrever no contador de linhas.

            val alinharCentro = SimpleAttributeSet()
            StyleConstants.setAlignment(alinharCentro, StyleConstants.ALIGN_CENTER)
            contadorDeLinhas.setParagraphAttributes(alinharCentro, true) // atributo de parágrafo do contador de linhas

            return contadorDeLinhas
        }

        private var ultimaContagemLinhas = 0

        private fun atualizarContadorDeLinhas() {
            val doc = contadorDeLinhas.styledDocument

            val docStyle = SimpleAttributeSet()
            StyleConstants.setForeground(docStyle, Color.WHITE)
            StyleConstants.setFontSize(docStyle, 17)

            // FIXME isso não funciona, n sei pq
            val contagemAtual = getContagemLinhas()
            if(ultimaContagemLinhas < contagemAtual) {
                doc.remove(0,doc.length)
            }
            for (linha in ultimaContagemLinhas until contagemAtual) {
                doc.insertString(ultimaContagemLinhas +1, "${linha + 1} ", docStyle)
            }
            ultimaContagemLinhas = contagemAtual

//            // codigo antigo... isso funcionava, tbm n compreendo... (é para ser a mesma coisa que o de cima)
//            for (linha in 0..getContagemLinhas())
//                doc.insertString(0, "${linha + 1} ", docStyle)
//            }
        }

        private fun getContagemLinhas(): Int {
            return this.areaDeEscrita.document.defaultRootElement.elementCount
        }
    }
}