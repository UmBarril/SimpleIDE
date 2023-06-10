package IDE

import IDE.util.ResourcesManager.getIcon
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.io.File
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

class CaminhoNaoValidoException(caminho: String) : Exception("Caminho inserido (\"$caminho\"não é válido")

class ArquivoNode(val arquivo: File) : DefaultMutableTreeNode(arquivo.name, arquivo.isDirectory) {
    fun carregarTodosOsSubArquivos() {
        arquivo.listFiles()?.forEach {
            add(ArquivoNode(it))
        }
    }
}

// FIXME
//  - PROIBIR DE ABRIR ARQUIVOS DO SISTEMA
//  - ESCONDER DOTFILES
class ExploradorDeArquivos(pastaParaAbrir: File, dimensao: Dimension) : JPanel() {
    private val arvore: JTree
    private val scrollPane: JScrollPane
    private val listeners = mutableListOf<(File) -> Unit>()

    constructor(pastaParaAbrir: String, dimensao: Dimension) : this(File(pastaParaAbrir), dimensao)

    init {
        background = Color(100,50, 140)
        preferredSize = dimensao
        minimumSize = dimensao

        if (!pastaParaAbrir.isDirectory) {
            throw CaminhoNaoValidoException(pastaParaAbrir.absolutePath)
        }

        val modelo = DefaultTreeModel(ArquivoNode(pastaParaAbrir))
        val raiz = modelo.root as ArquivoNode

        raiz.carregarTodosOsSubArquivos()

        arvore = JTree(raiz).apply {
            background = Color(65, 65, 75)
        }
        scrollPane = JScrollPane(this.arvore).apply {
//            background = Color(65, 65, 75)
//            setUI(basicScrollBarUI()) isso ta crashando a janela por algum motivo. Implementar no futuro
        }
        scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS

        this.add(scrollPane)
        arvore.apply {
            addTreeSelectionListener { // carregamento Lazy das pastas
                val nodeSelecionado = arvore.lastSelectedPathComponent as ArquivoNode
                if (nodeSelecionado.allowsChildren) {
                    nodeSelecionado.carregarTodosOsSubArquivos()
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
    }

    // Adicionar um listener que será acionado quando algum elemento dos arquivos for sleecionado
    fun adicionarArquivoSelecionadoListener(listener: (File) -> Unit) {
        listeners.add(listener)
    }

    fun atualizarDimensao(largura: Int) {
        scrollPane.preferredSize = Dimension(largura, this.height)
        arvore.preferredSize = scrollPane.size
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
