package IDE

import IDE.util.ResourcesManager.getIcon
import java.awt.Component
import java.awt.Dimension
import java.io.File
import java.util.*
import javax.swing.*
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

class CaminhoNaoValidoException(caminho: String) : Exception("Caminho inserido (\"$caminho\"não é válido")

// Seleciona o arquivo que o usuário clicou
// pega todos os arquivos do arquivo que o usuário
// selecionou(caso o arquivo for uma pasta) em formato de DefaultMutableTreeNode
// no caso, o arquivo que o usuário selecionou é o arquivo da linha 30
// caso o source do evento não for null, adicione o DefaultMutableTreeNode nele
// pega o source do evento como um Default mutable tree(pode ser null)//        raiz.listFiles()?.forEach { arquivo ->
//            if (arquivo.isDirectory) raizNode.add(DefaultMutableTreeNode(arquivo.name, true))
//            else raizNode.add(DefaultMutableTreeNode(arquivo.name, false))
//        }

class ArquivoNode(val arquivo: File) : DefaultMutableTreeNode(arquivo.name, arquivo.isDirectory) {
    fun carregarTodosOsSubArquivos() {
        arquivo.listFiles()?.forEach {
            add(ArquivoNode(it))
        }
    }
}

class ExploradorDeArquivos(pastaParaAbrir: File, dimensao: Dimension) : JPanel() {
    private val arvore: JTree
    private val scrollPane: JScrollPane
    private val listeners = mutableListOf<(File) -> Unit>()

    constructor(pastaParaAbrir: String, dimensao: Dimension) : this(File(pastaParaAbrir), dimensao)

    init {
        preferredSize = dimensao
        minimumSize = dimensao

        if (!pastaParaAbrir.isDirectory) {
            throw CaminhoNaoValidoException(pastaParaAbrir.absolutePath)
        }

        val modelo = DefaultTreeModel(ArquivoNode(pastaParaAbrir))
        val raiz = modelo.root as ArquivoNode

        raiz.carregarTodosOsSubArquivos()

        arvore = JTree(raiz)
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
        scrollPane = JScrollPane(this.arvore)
        this.add(scrollPane)

        modelo.reload()
    }

    // Adicionar um listener que será acionado quando algum elemento dos arquivos for sleecionado
    fun adicionarArquivoSlecionadoListener(listener: (File) -> Unit) {
        listeners.add(listener)
    }

    fun atualizarDimensao(largura: Int) {
        scrollPane.preferredSize = Dimension(largura - 25, this.height)
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
}
