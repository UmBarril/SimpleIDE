package IDE

import IDE.util.ResourcesManager.getIcon
import java.awt.*
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.ScrollPaneConstants
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

class CaminhoNaoValidoException(caminho: String) : Exception("Caminho inserido (\"$caminho\"não é válido")

class ArquivoNode(val arquivo: File) : DefaultMutableTreeNode(arquivo.name, arquivo.isDirectory) {
    fun carregarTodosOsSubArquivos(modelo: DefaultTreeModel) {
        arquivo.listFiles()?.forEach {
            if(!it.isHidden)
                modelo.insertNodeInto(ArquivoNode(it), this, this.childCount)
        }
    }
}

// FIXME
//  - PROIBIR DE ABRIR ARQUIVOS DO SISTEMA (OK)
//  - ESCONDER DOTFILES
class ExploradorDeArquivos(pastaParaAbrir: File, dimensao: Dimension) : JPanel(GridLayout()) {
    private val arvore: JTree
    private val scrollPane: JScrollPane
    private val listeners = mutableListOf<(File) -> Unit>()
    private val modelo = DefaultTreeModel(ArquivoNode(pastaParaAbrir))
    constructor(pastaParaAbrir: String, dimensao: Dimension) : this(File(pastaParaAbrir), dimensao)

    init {
        background = Color(100,50, 140)
        preferredSize = dimensao

        if (!pastaParaAbrir.isDirectory) {
            throw CaminhoNaoValidoException(pastaParaAbrir.absolutePath)
        }
        val raiz = modelo.root as ArquivoNode

        raiz.carregarTodosOsSubArquivos(modelo)

        arvore = object: JTree(raiz) {
            override fun getPreferredScrollableViewportSize(): Dimension? {
                return preferredSize
            }
        }
        arvore.background = Color(65, 65, 75)

        scrollPane = JScrollPane().apply {
//            background = Color(65, 65, 75)
//            setUI(basicScrollBarUI()) isso ta crashando a janela por algum motivo. Implementar no futuro
            verticalScrollBar.preferredSize = Dimension(10, 0)
            horizontalScrollBar.preferredSize = Dimension(0, 10)
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            verticalScrollBar.unitIncrement = 12;
        }
        arvore.apply {
            preferredSize = dimensao
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

    fun atualizarDimensao(largura: Int) {
//        scrollPane.preferredSize = Dimension(largura, this.height)
//        arvore.preferredSize = scrollPane.size
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
            arquivoNode.arquivo.endsWith(".java") -> getIcon("fugue-icons-3.5.6/icons/cup.png")
            arquivoNode.arquivo.endsWith(".php") -> getIcon("fugue-icons-3.5.6/icons/script-php.png")
            arquivoNode.arquivo.endsWith(".docx") -> getIcon("fugue-icons-3.5.6/icons/script-word.png")
            arquivoNode.arquivo.endsWith(".swf") -> getIcon("fugue-icons-3.5.6/icons/script-word.png")
            arquivoNode.arquivo.endsWith(".kt") -> getIcon("fugue-icons-3.5.6/icons/script-attribute-k.png")
            arquivoNode.arquivo.endsWith(".c") -> getIcon("fugue-icons-3.5.6/icons/script-attribute-c.png")
            arquivoNode.arquivo.endsWith(".exe") -> getIcon("fugue-icons-3.5.6/icons/document-binary.png")
            arquivoNode.arquivo.endsWith(".cs") -> getIcon("fugue-icons-3.5.6/icons/script-visual-studio.png")
            arquivoNode.arquivo.endsWith(".json") -> getIcon("fugue-icons-3.5.6/icons/json.png")
            arquivoNode.arquivo.endsWith(".html") -> getIcon("fugue-icons-3.5.6/icons/script-code.png")
            arquivoNode.arquivo.endsWith(".js") -> getIcon("fugue-icons-3.5.6/icons/script-text.png")
            arquivoNode.arquivo.endsWith(".db") -> getIcon("fugue-icons-3.5.6/icons/database.png")
            arquivoNode.arquivo.endsWith(".sql") -> getIcon("fugue-icons-3.5.6/icons/database-sql.png")
            arquivoNode.arquivo.endsWith(".pdf") -> getIcon("fugue-icons-3.5.6/icons/document-pdf-text.png")
            else -> getIcon("fugue-icons-3.5.6/icons/report-paper.png")
        }
        return this
    }

    override fun getBackgroundSelectionColor(): Color = Color(100,50,140)
    override fun getBackgroundNonSelectionColor(): Color = Color(65,65,75)
    override fun getForeground(): Color = Color.WHITE
}
