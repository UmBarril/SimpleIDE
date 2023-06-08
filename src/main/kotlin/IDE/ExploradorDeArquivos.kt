package IDE

import IDE.util.ResourcesManager.getIcon
import java.awt.Component
import java.awt.Dimension
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

class ExploradorDeArquivos(caminhoInicial: String, dimensao: Dimension) : JPanel() {

    private var arvore: JTree
    private val raiz: DefaultMutableTreeNode
    private val scrollPane: JScrollPane

    init {
        this.preferredSize = dimensao
        this.minimumSize = dimensao
        this.isVisible = true
        this.isOpaque = true

        raiz = arquivosDaRaiz(File(caminhoInicial))

        arvore = JTree(raiz).apply {
            addTreeSelectionListener { selectEvent ->
                val source = selectEvent.source as? DefaultMutableTreeNode // pega o source do evento como um Default mutable tree(pode ser null)
                File(selectEvent.path.toString()). // Seleciona o arquivo que o usuário clicou

                    carregarArquivosDaPasta()?. // pega todos os arquivos do arquivo que o usuário
                                                // selecionou(caso o arquivo for uma pasta) em formato de DefaultMutableTreeNode
                                                // no caso, o arquivo que o usuário selecionou é o arquivo da linha 30

                    let{source?.add(it)} // caso o source do evento não for null, adicione o DefaultMutableTreeNode nele
            }
            cellRenderer = RenderizadorDeNodes()
            preferredSize = dimensao
            isVisible = true
            isOpaque = true
        }

        scrollPane = JScrollPane(arvore).apply {
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
            if (arquivo.isDirectory) raizNode.add(DefaultMutableTreeNode(arquivo.name, true))
            else raizNode.add(DefaultMutableTreeNode(arquivo.name, false))
        }
        return raizNode
    }

    fun atualizarDimensao(largura: Int) {
        scrollPane.preferredSize = Dimension(largura-25, this.height)
        arvore.preferredSize = scrollPane.size
        println("largura do scrollPane: ${scrollPane.size.width}")

    }

    private fun File.carregarArquivosDaPasta(): DefaultMutableTreeNode? {
        if (!this.isDirectory) return null

        return (
            DefaultMutableTreeNode().apply {
                this@carregarArquivosDaPasta.listFiles()?.forEach { arquivo -> // para cada arquivo da pasta
                    if (arquivo.isDirectory) this.add(DefaultMutableTreeNode(arquivo.name, true)) // caso for pasta, adicione o node dessa forma
                    else this.add(DefaultMutableTreeNode(arquivo.name, false)) // caso contrario, adicione o node dessa forma.
                }
            }
        )

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

            if (value is DefaultMutableTreeNode) {
                icon = if (value.allowsChildren) getIcon("fugue-icons-3.5.6/icons/folder.png")
                else if (value.toString().endsWith(".java")) getIcon("fugue-icons-3.5.6/icons/cup.png")
                else getIcon("fugue-icons-3.5.6/icons/report-paper.png")
            }
            return this
        }
    }

}







