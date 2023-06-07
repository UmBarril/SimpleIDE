package IDE

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class JanelaPrincipal(titulo: String, largura: Int, altura: Int) : JFrame(titulo) {
    private var pastaAberta = System.getProperty("user.home")

    private val explorador: ExploradorDeArquivos
    private val editor: EditorDeTextoComAbas // painel que fica a direita ou no meio

    private val config: ConfigManager

    init {
        layout = GridLayout()

        size = Dimension(largura, altura)
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.background = Color(75, 75, 85)
        iconImage = ImageIcon("").image // TODO
        jMenuBar = criarMenuBar()

        config = ConfigManager()
        config.carregar()

        val tamanhoExplorador = Dimension((size.width * 0.2).roundToInt(), size.height - jMenuBar.height)
        val tamanhoEditor = Dimension((size.width * 0.8).roundToInt(), size.height - jMenuBar.height)

        explorador = ExploradorDeArquivos(pastaAberta, tamanhoExplorador)
        editor = EditorDeTextoComAbas(tamanhoEditor)

        explorador.addTreeSelectionListener { editor.abrirAquivo(it.path.toString()) }

        add(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.explorador, this.editor))
        pack()
    }

    private fun criarMenuBar(): JMenuBar {
        val menuBar = JMenuBar()
        menuBar.adicionarVarios(
            JMenu("Arquivo").apply {
                mnemonic = KeyEvent.VK_A
                adicionarVarios(
                    JMenuItem("Abrir").apply { // TODO: mostrar arquivos que foram abertos recentemente
                        addActionListener(::clicouBotaoAbrir)
                    },
                    JMenuItem("Salvar").apply {
                        addActionListener(::clicouBotaoSalvar)
                    },
                    JMenuItem("Salvar Como").apply {
                        addActionListener(::clicouBotaoSalvarComo)
                    },
                    JMenuItem("Sair").apply {
                        toolTipText = "Sair da IDE"
                        addActionListener {
                            // TODO: if(codigo nao salvo) { perguntar se tem que salvar }
                            // JOptionPane.showConfirmDialog(this@JanelaPrincipal, "")
                            exitProcess(0)
                        }
                    }
                )
            },
            JMenu("Janela").apply {
                mnemonic = KeyEvent.VK_J
                adicionarVarios(
                    JMenu("Mudar Tema").apply {
                        adicionarVarios(
                            JRadioButtonMenuItem("Tema Escuro").apply { addActionListener { mudarTema(TemaIDE.ESCURO)} },
                            JRadioButtonMenuItem("Tema Claro").apply { addActionListener { mudarTema(TemaIDE.CLARO)} }
                        )
                    }
                )
            },
            JMenu("Configurações").apply {
                mnemonic = KeyEvent.VK_C
                add(
                    JMenuItem("Abrir").apply {
                        addActionListener(::clicouBotaoAbrirConfiguracoes)
                    }
                )
            }
        )
        return menuBar
    }

    private fun clicouBotaoAbrir(e: ActionEvent) {
        val fileChooser = JFileChooser(pastaAberta)

        val resultado = fileChooser.showOpenDialog(this@JanelaPrincipal)
        val f = File(fileChooser.selectedFile.absolutePath)
        if (resultado == JFileChooser.APPROVE_OPTION) {
            editor.abrirAquivo(f)
        }
    }

    private fun clicouBotaoSalvar(e: ActionEvent) {
        val caminho = editor.arquivoAberto
        if (caminho != null) {
            editor.salvarArquivo(caminho)
        } else {
            clicouBotaoSalvarComo(null)
        }
    }

    private fun clicouBotaoSalvarComo(e: ActionEvent?) {
        val fileChooser = JFileChooser(pastaAberta)

        val resultado = fileChooser.showOpenDialog(this@JanelaPrincipal)
        if(resultado == JFileChooser.APPROVE_OPTION) {
            editor.salvarArquivo(File(fileChooser.selectedFile.absolutePath))
        }
    }

    private fun mudarTema(tema: TemaIDE) {
        when(tema) {
            TemaIDE.CLARO -> {
                this@JanelaPrincipal.contentPane.background = Color(175, 175, 190)
                this@JanelaPrincipal.contentPane.background = Color(75, 75, 85)
            }
            TemaIDE.ESCURO -> {
                this@JanelaPrincipal.contentPane.background = Color(75, 75, 85)
                this@JanelaPrincipal.contentPane.background = Color(75, 75, 85)
            }
        }
    }

    private fun clicouBotaoAbrirConfiguracoes(e: ActionEvent) {
        val janelaConfiguracoes = JDialog(this, "Configurações")
        janelaConfiguracoes.layout = GridLayout(3,1)

        janelaConfiguracoes.size = Dimension(this.width / 4, this.height / 4)
        if (janelaConfiguracoes.height < 100) {
            janelaConfiguracoes.size = Dimension(janelaConfiguracoes.width, 100)
        }
        if (janelaConfiguracoes.width < 100) {
            janelaConfiguracoes.size = Dimension(100, janelaConfiguracoes.height)
        }

//        val sliderTamanhoFonte = JSlider(0, 100)
        val spinnerTamanhoFonte = JSpinner()
        spinnerTamanhoFonte.size = Dimension(20, 20)
        val botaoSalvar = JButton("Salvar")
        botaoSalvar.isEnabled = false
        val botaoCancelar = JButton("Cancelar")

        spinnerTamanhoFonte.value = config["editorTamanhoFonte"].toInt()
        spinnerTamanhoFonte.addChangeListener {
            botaoSalvar.isEnabled = true
        }
        botaoSalvar.addActionListener {
            config["editorTamanhoFonte"] = spinnerTamanhoFonte.value.toString()
            config.salvar()
            janelaConfiguracoes.dispose()
        }
        botaoCancelar.addActionListener {
            janelaConfiguracoes.dispose()
        }
        val label = JLabel("Tamanho da fonte: ")
        janelaConfiguracoes.add(label)
        janelaConfiguracoes.add(spinnerTamanhoFonte)
        janelaConfiguracoes.add(
            JPanel(GridLayout(1,2, 2, 2)).apply {
                adicionarVarios(botaoSalvar,botaoCancelar)
            }
        )

        janelaConfiguracoes.isVisible = true
    }

    // Facilitador para não ter que fazer vários adds em sequência
    private fun JComponent.adicionarVarios(vararg components: JComponent) {
        components.forEach { comp ->
            this.add(comp)
        }
    }
}

enum class TemaIDE {
    CLARO, ESCURO
}
