package barril.ide

import barril.ide.view.FramePrincipal
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*

fun main() {
    Toolkit.getDefaultToolkit().setDynamicLayout(true)

    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        println("Não foi encontrado WindowsLookAndFeel")
        e.printStackTrace()
    }

    val telaDeCarregamento = JWindow().apply {
        add(JLabel(ResourcesUtil.getIcon("bskLogo.png")))
        background = Color(0,0,0,0)

        pack()
        isVisible = true
        setLocationRelativeTo(null)
    }

    ConfigManager.inicializar()

    SwingUtilities.invokeLater { // invokeLater para não haver problemas com Threads mais tarde...
        try{
            val alturaJanela = Toolkit.getDefaultToolkit().screenSize.height
            val larguraJanela = Toolkit.getDefaultToolkit().screenSize.width

            val janelaPrincipal = FramePrincipal(Dimension(larguraJanela, alturaJanela))
            janelaPrincipal.isVisible = true
        } catch(e: Exception) {
            JOptionPane.showConfirmDialog(null, "Erro ao iniciar o programa! Veja o STDOUT para analisar o erro.", "Erro!", JOptionPane.OK_CANCEL_OPTION)
            e.printStackTrace()
        } finally {
            telaDeCarregamento.dispose()
        }
    }
}