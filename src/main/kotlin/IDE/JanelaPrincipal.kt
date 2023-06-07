package IDE

import java.awt.Button
import java.awt.Color
import java.awt.Dimension
import javax.swing.*

// TODO: explorador de arquivos
// TODO: editor de texto
// TODO: terminal integrado

class Janela(titulo: String, largura: Int, altura: Int) : JFrame(titulo) {

    init {
        isVisible = true
        size = Dimension(largura, altura)
        background = Color.BLACK

        this.add(criarMenuBar())

    }

    private fun criarMenuBar(): JMenu {
        val menu = JMenu("O menu")
        menu.add(JMenuItem("teste"))
        menu.addSeparator()
        val bg = ButtonGroup().apply {
            add(
                Button("configs").apply {
                    isVisible = true
                    size = Dimension(20,20)
                }
            )
        }


        return menu
    }



}