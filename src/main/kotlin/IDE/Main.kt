package IDE

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Toolkit
import javax.swing.*

fun main() {
    val telaDeCarregamento = JWindow().apply { // IDEA: Colocar imagem de carregamento aqui
        add(JLabel("Carregando...").apply { font = Font("Arial", Font.BOLD, 50); })
        background = Color(0,0,0,0)

        pack()
        isVisible = true
        setLocationRelativeTo(null)
    }

    SwingUtilities.invokeLater { // invokeLater para não haver problemas com Threads mais tarde...
        try {
            UIManager.setLookAndFeel("java.swing.plaf.windows.WindowLookAndFeel")
        } catch (e: Exception) {
            println("Não foi encontrado WindowsLookAndFeel")
            e.printStackTrace()
        }
        val alturaJanela = Toolkit.getDefaultToolkit().screenSize.height
        val larguraJanela = Toolkit.getDefaultToolkit().screenSize.width

        val janela = JanelaPrincipal("Teste", larguraJanela, alturaJanela)
        telaDeCarregamento.dispose()

        janela.isVisible = true
    }
}