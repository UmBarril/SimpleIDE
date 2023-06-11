import barril.ide.ConfigManager
import barril.ide.ResourcesUtil
import barril.ide.exception.ConfigNaoInicializadaException
import barril.ide.view.EditorDeTextoComAbasPanel
import barril.ide.view.ExploradorDeArquivosPanel
import barril.ide.view.FramePrincipal
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.Dimension
import java.awt.image.PixelGrabber
import java.io.File
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class Test {
    @BeforeAll
    fun setUp() {
        assertThrows<ConfigNaoInicializadaException> { FramePrincipal(Dimension(1,1)) }
        ConfigManager.inicializar()
    }
    @Test
    fun testRenderizadorDeNodes() {
        val renderizadorDeNodes = ExploradorDeArquivosPanel.RenderizadorDeNodes()
        var modelo = DefaultTreeModel(ExploradorDeArquivosPanel.ArquivoNode(File("test.java")))
        renderizadorDeNodes.getTreeCellRendererComponent(JTree(modelo), modelo.root,
            sel = false,
            expanded = false,
            leaf = false,
            row = 1,
            hasFocus = false
        )
        assertTrue(compareImageIcons(renderizadorDeNodes.icon as ImageIcon, ResourcesUtil.getIcon("fugue-icons-3.5.6/icons/cup.png")))

        modelo = DefaultTreeModel(ExploradorDeArquivosPanel.ArquivoNode(File("test.pdf")))
        renderizadorDeNodes.getTreeCellRendererComponent(JTree(modelo), modelo.root,
            sel = false,
            expanded = false,
            leaf = false,
            row = 1,
            hasFocus = false
        )
        assertTrue(compareImageIcons(renderizadorDeNodes.icon as ImageIcon, ResourcesUtil.getIcon("fugue-icons-3.5.6/icons/document-pdf-text.png")))
    }

    @Test
    fun testarEditorDeTextoComAbas() {
        val editorDeTextoComAbas = EditorDeTextoComAbasPanel(Dimension(10,10))
        editorDeTextoComAbas.criarAbaVazia("0")
        editorDeTextoComAbas.criarAbaVazia("1")
        editorDeTextoComAbas.criarAbaVazia("2")
        editorDeTextoComAbas.criarAbaVazia("3")
        editorDeTextoComAbas.criarAbaVazia("4")
        assertEquals(editorDeTextoComAbas.quantidadeDeAbas, 6)
        assertEquals(editorDeTextoComAbas.quantidadeDeAbas, 6)
    }

    @Test
    fun testCriarAbaVazia() {
        val panel = EditorDeTextoComAbasPanel(Dimension(800, 600))
        val nomeAba = "Nova Aba"

        panel.criarAbaVazia(nomeAba)

        assertEquals(2, panel.quantidadeDeAbas) // Verifica se a quantidade de abas é 1
        assertEquals(null, panel.caminhoDoArquivoAberto) // Verifica se a aba aberta possui o nome esperado
    }

    @Test
    fun testCriarTelaInicial() {
        val panel = EditorDeTextoComAbasPanel(Dimension(800, 600))

        val telaInicial = panel.criarTelaInicial()

        assertTrue(telaInicial is JPanel) // Verifica se o retorno é um JPanel
        assertEquals(2, telaInicial.componentCount) // Verifica se o JPanel possui 2 componentes (label e botão)
        assertTrue(telaInicial.getComponent(1) is JButton) // Verifica se o segundo componente é um JButton
    }

    fun compareImageIcons(icon1: ImageIcon, icon2: ImageIcon): Boolean {
        val image1 = icon1.image
        val image2 = icon2.image

        if (image1 == null || image2 == null) {
            return false
        }

        // Comparar pixel por pixel
        val width = image1.getWidth(null)
        val height = image1.getHeight(null)

        if (width != image2.getWidth(null) || height != image2.getHeight(null)) {
            return false
        }

        val pixels1 = IntArray(width * height)
        val pixels2 = IntArray(width * height)

        val pixelGrabber1 = PixelGrabber(image1, 0, 0, width, height, pixels1, 0, width)
        val pixelGrabber2 = PixelGrabber(image2, 0, 0, width, height, pixels2, 0, width)

        pixelGrabber1.grabPixels()
        pixelGrabber2.grabPixels()

        for (i in pixels1.indices) {
            if (pixels1[i] != pixels2[i]) {
                return false
            }
        }
        return true
    }
}