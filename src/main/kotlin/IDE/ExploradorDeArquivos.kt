package IDE

import java.awt.Dimension
import java.awt.PopupMenu
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.SwingUtilities
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
        raiz = carregarRaiz(File(caminhoInicial))

        arvore = JTree(raiz).apply {
            preferredSize = dimensao
            isVisible = true
            addTreeSelectionListener { clickedComponent ->
                val arquivo = File(clickedComponent.path.toString())
                if (arquivo.isDirectory) {
                    arquivosDaPasta(arquivo).forEach {

                    }
                }
            }
        }

        arvore.addHierarchyListener {}
        arvore = JTree(carregarRaiz(File(caminhoInicial)))

        // Código para adicionar a funcionalidade de menu de contexto no explorador.
        val popupMenu = PopupMenu() // adicionar coisas a isso
        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {
                e ?: throw NullPointerException()

                if (SwingUtilities.isRightMouseButton(e)) {
                    val row = arvore.getClosestRowForLocation (e.x, e.y);
                    arvore.setSelectionRow(row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            override fun mousePressed(e: MouseEvent?) {
                TODO("Not yet implemented")
            }
            override fun mouseReleased(e: MouseEvent?) {
                TODO("Not yet implemented")
            }
            override fun mouseEntered(e: MouseEvent?) {
                TODO("Not yet implemented")
            }
            override fun mouseExited(e: MouseEvent?) {
                TODO("Not yet implemented")
            }
        })
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
    private fun carregarRaiz(raiz: File): DefaultMutableTreeNode {
        if (!raiz.isDirectory) return DefaultMutableTreeNode(raiz.name)
        val raizNode = DefaultMutableTreeNode(raiz.name, true).apply {
            raiz.listFiles()?.forEach { file ->
                if (file.isDirectory) add(DefaultMutableTreeNode(file.name, true))
                else add(DefaultMutableTreeNode(file.name, false))
            }
        }

        return raizNode
    }

    private fun arquivosDaPasta(pasta: File): List<DefaultMutableTreeNode> {
        val listaDeArquivos = mutableListOf<DefaultMutableTreeNode>()
        pasta.listFiles()?.forEach { file ->
            if (file.isDirectory) listaDeArquivos.add(DefaultMutableTreeNode(file.name, true))
            else listaDeArquivos.add(DefaultMutableTreeNode(file.name, false))
        }
        return listaDeArquivos
    }
}







