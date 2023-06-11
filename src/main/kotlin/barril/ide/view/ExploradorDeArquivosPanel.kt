package barril.ide.view

import barril.ide.exception.CaminhoNaoValidoException
import barril.ide.ResourcesUtil.getIcon
import java.awt.*
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.ScrollPaneConstants
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

// FIXME
//  - PROIBIR DE ABRIR ARQUIVOS DO SISTEMA (OK)
//  - ESCONDER DOTFILES
class ExploradorDeArquivosPanel(pastaParaAbrir: File, dimensao: Dimension) : JPanel(GridLayout()) {
    private val arvore: JTree
    private val scrollPane: JScrollPane
    private val listeners = mutableListOf<(File) -> Unit>()
    private val modelo = DefaultTreeModel(ArquivoNode(pastaParaAbrir))

    constructor(pastaParaAbrir: String, dimensao: Dimension) : this(File(pastaParaAbrir), dimensao)

    // FIXME apenas isso não é suficiente para que arquivos de pastas já abertas sejam reconhecidos. Procurar solução
    fun recarregarArvore() = modelo.reload()

    init {
        background = Color(100,50, 140)
        val cor = Color(65, 65, 75)

        if (!pastaParaAbrir.isDirectory) {
            throw CaminhoNaoValidoException(pastaParaAbrir.absolutePath)
        }
        val raiz = modelo.root as ArquivoNode

        raiz.carregarTodosOsSubArquivos(modelo)

        arvore = JTree(raiz)
        arvore.background = cor

        scrollPane = JScrollPane().apply {
//            setUI(basicScrollBarUI()) isso ta crashando a janela por algum motivo. Implementar no futuro
            verticalScrollBar.preferredSize = Dimension(10, 0)
            horizontalScrollBar.preferredSize = Dimension(0, 10)
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            verticalScrollBar.unitIncrement = 12;
        }
        arvore.apply {
            addTreeSelectionListener { // carregamento Lazy das pastas
                val nodeSelecionado = arvore.lastSelectedPathComponent as ArquivoNode
                if (nodeSelecionado.allowsChildren) {
                    nodeSelecionado.carregarTodosOsSubArquivos(modelo)
                } else {
                    listeners.forEach {
                        it.invoke(nodeSelecionado.arquivo)
                    }
                }
                modelo.reload()
            }
            cellRenderer = RenderizadorDeNodes()
        }
        modelo.reload()

        val panel = ScrollablePanel()
        val constraints = GridBagConstraints()
        panel.background = cor
        constraints.fill = GridBagConstraints.BOTH
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        panel.add(arvore, constraints)
        scrollPane.setViewportView(panel)

        this.add(scrollPane)
    }

    // Adicionar um listener que será acionado quando algum elemento dos arquivos for sleecionado
    fun adicionarArquivoSelecionadoListener(listener: (File) -> Unit) {
        listeners.add(listener)
    }

    class ArquivoNode(val arquivo: File) : DefaultMutableTreeNode(arquivo.name, arquivo.isDirectory) {
        fun carregarTodosOsSubArquivos(modelo: DefaultTreeModel) {
            arquivo.listFiles()?.forEach {
                if(!it.isHidden)
                    modelo.insertNodeInto(ArquivoNode(it), this, this.childCount)
            }
        }
    }

    class RenderizadorDeNodes : DefaultTreeCellRenderer() {
        override fun getTreeCellRendererComponent(
            tree: JTree?,
            value: Any?,
            sel: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ): Component {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)

            val arquivoNode = value as ArquivoNode
            icon = when {
                arquivoNode.allowsChildren -> getIcon("fugue-icons-3.5.6/icons/folder.png")
                arquivoNode.arquivo.path.endsWith(".md") -> getIcon("fugue-icons-3.5.6/icons/book-open-bookmark.png")
                arquivoNode.arquivo.path.endsWith(".java") -> getIcon("fugue-icons-3.5.6/icons/cup.png")
                arquivoNode.arquivo.path.endsWith(".cfg") -> getIcon("fugue-icons-3.5.6/icons/gear.png")
                arquivoNode.arquivo.path.endsWith(".properties") -> getIcon("fugue-icons-3.5.6/icons/gear.png")
                arquivoNode.arquivo.path.endsWith(".ini") -> getIcon("fugue-icons-3.5.6/icons/gear.png")
                arquivoNode.arquivo.path.endsWith(".php") -> getIcon("fugue-icons-3.5.6/icons/script-php.png")
                arquivoNode.arquivo.path.endsWith(".docx") -> getIcon("fugue-icons-3.5.6/icons/script-word.png")
                arquivoNode.arquivo.path.endsWith(".epub") -> getIcon("fugue-icons-3.5.6/icons/document-epub-text.png")
                arquivoNode.arquivo.path.endsWith(".swf") -> getIcon("fugue-icons-3.5.6/icons/document-flash-movie.png")
                arquivoNode.arquivo.path.endsWith(".kt") -> getIcon("fugue-icons-3.5.6/icons/script-attribute-k.png")
                arquivoNode.arquivo.path.endsWith(".c") -> getIcon("fugue-icons-3.5.6/icons/script-attribute-c.png")
                arquivoNode.arquivo.path.endsWith(".exe") -> getIcon("fugue-icons-3.5.6/icons/document-binary.png")
                arquivoNode.arquivo.path.endsWith(".cs") -> getIcon("fugue-icons-3.5.6/icons/script-visual-studio.png")
                arquivoNode.arquivo.path.endsWith(".json") -> getIcon("fugue-icons-3.5.6/icons/json.png")
                arquivoNode.arquivo.path.endsWith(".html") -> getIcon("fugue-icons-3.5.6/icons/script-code.png")
                arquivoNode.arquivo.path.endsWith(".js") -> getIcon("fugue-icons-3.5.6/icons/script-text.png")
                arquivoNode.arquivo.path.endsWith(".db") -> getIcon("fugue-icons-3.5.6/icons/database.png")
                arquivoNode.arquivo.path.endsWith(".sql") -> getIcon("fugue-icons-3.5.6/icons/database-sql.png")
                arquivoNode.arquivo.path.endsWith(".pdf") -> getIcon("fugue-icons-3.5.6/icons/document-pdf-text.png")
                arquivoNode.arquivo.path.endsWith(".gitignore") -> getIcon("fugue-icons-3.5.6/icons/prohibition.png")
                arquivoNode.arquivo.path.endsWith(".sh") -> getIcon("fugue-icons-3.5.6/icons/application-terminal.png")
                arquivoNode.arquivo.path.endsWith(".bat") -> getIcon("fugue-icons-3.5.6/icons/application-terminal.png")
                arquivoNode.arquivo.path.endsWith(".ps1") -> getIcon("fugue-icons-3.5.6/icons/application-terminal.png")
                arquivoNode.arquivo.path.endsWith(".png") -> getIcon("fugue-icons-3.5.6/icons/images.png")
                arquivoNode.arquivo.path.endsWith(".jpg") -> getIcon("fugue-icons-3.5.6/icons/images.png")
                arquivoNode.arquivo.path.endsWith(".jpeg") -> getIcon("fugue-icons-3.5.6/icons/images.png")
                arquivoNode.arquivo.path.endsWith(".bmp") -> getIcon("fugue-icons-3.5.6/icons/images.png")
                arquivoNode.arquivo.path.endsWith(".mp3") -> getIcon("fugue-icons-3.5.6/icons/document-music.png")
                arquivoNode.arquivo.path.endsWith(".flac") -> getIcon("fugue-icons-3.5.6/icons/document-music.png")
                arquivoNode.arquivo.path.endsWith(".opus") -> getIcon("fugue-icons-3.5.6/icons/document-music.png")
                arquivoNode.arquivo.path.endsWith(".mp4") -> getIcon("fugue-icons-3.5.6/icons/film.png")
                arquivoNode.arquivo.path.endsWith(".mkv") -> getIcon("fugue-icons-3.5.6/icons/film.png")
                else -> getIcon("fugue-icons-3.5.6/icons/report-paper.png")
            }
            return this
        }

        override fun getBackgroundSelectionColor(): Color = Color(100,50,140)
        override fun getBackgroundNonSelectionColor(): Color = Color(65,65,75)
        override fun getForeground(): Color = Color.WHITE
    }
}

