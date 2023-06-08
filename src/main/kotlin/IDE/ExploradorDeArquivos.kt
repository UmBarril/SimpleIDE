package IDE

import java.awt.Component
import java.awt.Dimension
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.UIManager
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

class ExploradorDeArquivos(caminhoInicial: String, dimensao: Dimension) : JPanel() {

    private var arvore: JTree
    private val raiz: DefaultMutableTreeNode

    init {
        this.preferredSize = dimensao
        this.minimumSize = dimensao
        this.isVisible = true
        this.isOpaque = true

        raiz = arquivosDaRaiz(File(caminhoInicial))

        arvore = JTree(raiz).apply {
            cellRenderer = RenderizadorDeNodes()
            preferredSize = dimensao
            isVisible = true
            isOpaque = true
        }

        val scrollPane = JScrollPane(arvore).apply {
            preferredSize = dimensao
            isVisible = true
            isOpaque = true
        }

        this.add(scrollPane)

//        Código para adicionar a funcionalidade de menu de contexto no explorador.
//        val popupMenu = PopupMenu() // adicionar coisas a isso
//        addMouseListener(object : MouseListener {
//            override fun mouseClicked(e: MouseEvent?) {
//                e ?: throw NullPointerException()
//
//                if (SwingUtilities.isRightMouseButton(e)) {
//                    val row = arvore.getClosestRowForLocation (e.x, e.y);
//                    arvore.setSelectionRow(row);
//                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
//                }
//            }
//            override fun mousePressed(e: MouseEvent?) {
//                TODO("Not yet implemented")
//            }
//            override fun mouseReleased(e: MouseEvent?) {
//                TODO("Not yet implemented")
//            }
//            override fun mouseEntered(e: MouseEvent?) {
//                TODO("Not yet implemented")
//            }
//            override fun mouseExited(e: MouseEvent?) {
//                TODO("Not yet implemented")
//            }
//        })


    }

    fun addTreeSelectionListener(listener: TreeSelectionListener) {
        arvore.addTreeSelectionListener(listener)
    }

    private fun arquivosDaRaiz(raiz: File): DefaultMutableTreeNode {
        val raizNode = DefaultMutableTreeNode(raiz.name)
        if (!raiz.isDirectory) return raizNode

        raiz.listFiles()?.forEach { arquivo ->
            if (arquivo.isFile) raizNode.add(DefaultMutableTreeNode(arquivo.name))
        }
        return DefaultMutableTreeNode()
    }

    class RenderizadorDeNodes: DefaultTreeCellRenderer() {

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

            if (value is DefaultMutableTreeNode && value.allowsChildren) {
                icon = UIManager.getIcon("FileView.directoryIcon")
            }
            return this
        }
    }

}







