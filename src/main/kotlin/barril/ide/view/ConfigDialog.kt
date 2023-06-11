package barril.ide.view

import barril.ide.ResourcesUtil.getResource
import barril.ide.ConfigManager
import java.awt.Dimension
import java.awt.Frame
import java.awt.GridLayout
import javax.swing.*

class ConfigDialog(janelaPai: Frame, dimension: Dimension, aoFechar: () -> Unit) {
    private val janelaConfiguracoes = JDialog(janelaPai, "Configurações")

    init {
        janelaConfiguracoes.setIconImage(ImageIcon(getResource("fugue-icons-3.5.6/icons/gear.png")).image)
        janelaConfiguracoes.layout = GridLayout(3,1)

        janelaConfiguracoes.size = dimension
        if (janelaConfiguracoes.height < 100) {
            janelaConfiguracoes.size = Dimension(janelaConfiguracoes.width, 100)
        }
        if (janelaConfiguracoes.width < 100) {
            janelaConfiguracoes.size = Dimension(100, janelaConfiguracoes.height)
        }

        val spinnerTamanhoFonte = JSpinner()
        spinnerTamanhoFonte.size = Dimension(20, 20)

        val botaoSalvar = JButton("Salvar")
        botaoSalvar.isEnabled = false
        botaoSalvar.addActionListener {
            ConfigManager["editorTamanhoFonte"] = spinnerTamanhoFonte.value.toString()
            ConfigManager.salvar()
            aoFechar()
            janelaConfiguracoes.dispose()
        }

        val botaoCancelar = JButton("Cancelar")
        botaoCancelar.addActionListener {
            aoFechar()
            janelaConfiguracoes.dispose()
        }

        spinnerTamanhoFonte.value = ConfigManager["editorTamanhoFonte"]?.toInt()
        spinnerTamanhoFonte.addChangeListener {
            botaoSalvar.isEnabled = true
        }

        val label = JLabel("Tamanho da fonte: ")
        janelaConfiguracoes.add(label)
        janelaConfiguracoes.add(spinnerTamanhoFonte)
        janelaConfiguracoes.add(
            JPanel(GridLayout(1,2, 2, 2)).apply {
                adicionarVarios(botaoSalvar,botaoCancelar)
            }
        )
    }
    fun abrir() {
        janelaConfiguracoes.isVisible = true
    }
}