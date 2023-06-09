package IDE

import java.awt.Dimension
import java.awt.Font
import java.awt.Toolkit
import javax.swing.*

fun main() {
    val alturaJanela = Toolkit.getDefaultToolkit().screenSize.height
    val larguraJanela = Toolkit.getDefaultToolkit().screenSize.width
    Toolkit.getDefaultToolkit().setDynamicLayout(true)

    try {
//            UIManager.setLookAndFeel("java.swing.plaf.windows.WindowLookAndFeel")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        println("Não foi encontrado WindowsLookAndFeel")
        e.printStackTrace()
    }

    val telaDeCarregamento = JWindow().apply { // IDEA: Colocar imagem de carregamento aqui
        add(JLabel("Carregando...").apply { font = Font("Arial", Font.BOLD, 50); })
//        background = Color(0,0,0,0)

        pack()
        isVisible = true
        setLocationRelativeTo(null)
    }
    SwingUtilities.invokeLater { // invokeLater para não haver problemas com Threads mais tarde...
        try{
            val janelaPrincipal = JanelaPrincipal(Dimension(larguraJanela, alturaJanela))
            janelaPrincipal.isVisible = true
        } catch(e: Exception) {
            JOptionPane.showConfirmDialog(null, "Erro ao iniciar o programa! Veja o STDOUT para analisar o erro.", "Erro!", JOptionPane.OK_CANCEL_OPTION)
            e.printStackTrace()
        } finally {
            telaDeCarregamento.dispose()
        }
    }
}