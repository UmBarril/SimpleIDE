package IDE

import java.awt.Dimension
import java.io.File
import java.util.Deque
import java.util.LinkedList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode

class ExploradorDeArquivos(caminhoInicial: String, dimensao: Dimension) : JPanel() {

    private var arvore: JTree
    private val raiz: DefaultMutableTreeNode

    init {
        this.preferredSize = dimensao
        this.minimumSize = dimensao
        this.isOpaque = true
        this.isVisible = true
        raiz = bfsArvore(File(caminhoInicial))

        arvore = JTree(raiz).apply {
            preferredSize = dimensao
            isVisible = true
        }
        arvore.addHierarchyListener {
            arvore = JTree(bfsArvore(File(caminhoInicial)))
        }
        val scrollPane = JScrollPane().apply {
            size = dimensao
            add(arvore)
        }
        add(scrollPane)
    }

    fun addTreeSelectionListener(listener: TreeSelectionListener) {
        arvore.addTreeSelectionListener(listener)
    }

    /**
     * Algoritimo Breadth First Search para pegar todas as pastas
     * e retornar um DefaultMutableTreeNode contendo toda a
     * hierarquia de pastas e arquivos
     * @param raiz O diretório raiz do projeto.
     * @return o DefaultMutableTreeNode contendo a hierarquia de pastas e arquivos.
     * @author Slz
     */
    private fun bfsArvore(raiz: File): DefaultMutableTreeNode {
        if (!raiz.isDirectory) return DefaultMutableTreeNode(raiz.name)
        val visitado = DefaultMutableTreeNode(raiz.name, true)
        val visitar: Deque<File> = LinkedList()
        visitar.add(raiz)

        while(visitar.isNotEmpty()) {
            val arquivoAtual = visitar.removeFirst()
            val node = DefaultMutableTreeNode(arquivoAtual.name)
            arquivoAtual.listFiles()?.forEach {
                node.add(DefaultMutableTreeNode(it.name))
                visitar += it
            }
            visitado.add(node)
        }

        return visitado
    }

    private fun carregarArquivosDaPasta(pasta: File): DefaultMutableTreeNode {
        pasta.for
    }
}







