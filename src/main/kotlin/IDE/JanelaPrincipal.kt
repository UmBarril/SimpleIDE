package IDE

import java.awt.*
import java.awt.event.*
import java.io.File
import javax.swing.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class JanelaPrincipal(tamanho: Dimension) : JFrame("SimpleIDE") {
    private var pastaAberta = System.getProperty("user.home")

    private val explorador: ExploradorDeArquivos
    private val editor: EditorDeTextoComAbas // painel que fica a direita ou no meio

    private val config: ConfigManager = ConfigManager()

    init {
        config.carregar()

        layout = GridLayout()

        size = tamanho
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) = fecharProgramaSeConfirmar()
        })
        contentPane.background = Color(75, 75, 85)
        iconImage = ImageIcon("").image // TODO
        jMenuBar = criarMenuBar()


        val tamanhoExplorador = Dimension((size.width * 0.2).roundToInt(), size.height - jMenuBar.height)
        val tamanhoEditor = Dimension((size.width * 0.8).roundToInt(), size.height - jMenuBar.height)

        explorador = ExploradorDeArquivos(pastaAberta, tamanhoExplorador)
        editor = EditorDeTextoComAbas(tamanhoEditor)

        explorador.addTreeSelectionListener { editor.abrirAquivo(it.path.toString()) }

        add(
            JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.explorador, this.editor).apply {
                addPropertyChangeListener {
                    if (it.propertyName == "dividerLocation" || it.propertyName == "lastDividerLocation") {
                        println("valor: ${it.oldValue}")
                        explorador.atualizarDimensao(it.oldValue as Int)
                    }
                }
            }
        )
        pack()
    }

    private fun criarMenuBar(): JMenuBar {
        val menuBar = JMenuBar()
        menuBar.adicionarVarios(
            JMenu("Arquivo").apply {
                mnemonic = KeyEvent.VK_A
                adicionarVarios(
                    JMenuItem("Abrir...").apply {
                        toolTipText = "Abrir arquivo no sistema"
                        icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/folder-stand.png"))
                        addActionListener(::clicouBotaoAbrir)
                    },
                    JMenu("Abrir Recentes").also {
                        addMouseListener(object : MouseListener {
                            override fun mouseEntered(e: MouseEvent?) {
                                it.removeAll()
                                val arquivosRecentes = config["arquivosRecentes"].split(";")
                                if(arquivosRecentes.isEmpty()) {
                                    it.add(JMenuItem("Nunhum arquivo aberto ainda."))
                                    return
                                }
                                for(i in arquivosRecentes.indices) {
                                    it.add(JMenuItem("${i+1}: ${arquivosRecentes[i]}").apply {
                                        addActionListener {
                                            editor.abrirAquivo(arquivosRecentes[i])
                                        }
                                    })
                                }
                            }
                            override fun mouseClicked(e: MouseEvent?) {}
                            override fun mousePressed(e: MouseEvent?) {}
                            override fun mouseReleased(e: MouseEvent?) {}
                            override fun mouseExited(e: MouseEvent?) {}
                        })
                    },
                    JMenuItem("Salvar").apply {
                        icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/disk.png"))
                        addActionListener(::clicouBotaoSalvar)
                    },
                    JMenuItem("Salvar Como...").apply {
                        icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/disks.png"))
                        addActionListener(::clicouBotaoSalvarComo)
                    }
                )
                addSeparator()
                add(
                    JMenuItem("Sair").apply {
                        icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/door-open-out.png"))
                        toolTipText = "Sair da IDE"
                        addActionListener { fecharProgramaSeConfirmar() }
                    }
                )
            },
            JMenu("Janela").apply {
                mnemonic = KeyEvent.VK_J
                adicionarVarios(
                    JMenu("Mudar Tema").apply {
                        adicionarVarios(
                            JRadioButtonMenuItem("Tema Escuro").apply {
                                icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/flag-black.png"))
                                addActionListener { mudarTema(TemaIDE.ESCURO) }
                            },
                            JRadioButtonMenuItem("Tema Claro").apply {
                                icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/flag-white.png"))
                                addActionListener { mudarTema(TemaIDE.CLARO)}
                            }
                        )
                    }
                )
            },
            JMenu("Configurações").apply {
                mnemonic = KeyEvent.VK_C
                add(
                    JMenuItem("Abrir").apply {
                        icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/gear.png"))
                        addActionListener(::clicouBotaoAbrirConfiguracoes)
                    }
                )
            }
        )
        return menuBar
    }

    fun fecharProgramaSeConfirmar() {
        val resultado = JOptionPane.showConfirmDialog(this, "Você realmente deseja sair? Mudanças não salvas serão perdidas.", "Sair", JOptionPane.YES_NO_OPTION)
        if(resultado == JOptionPane.YES_NO_OPTION) {
            exitProcess(0)
        }
    }

    private fun clicouBotaoAbrir(e: ActionEvent) {
        val fileChooser = JFileChooser(pastaAberta)

        val resultado = fileChooser.showOpenDialog(this)
        val f = File(fileChooser.selectedFile.absolutePath)
        if (resultado == JFileChooser.APPROVE_OPTION) {
            editor.abrirAquivo(f)
            if(config["arquivosRecentes"].isEmpty()) {
                config["arquivosRecentes"] = f.path
            } else {
                val cincoMaisRecentes: List<String> = config["arquivosRecentes"].split(';', limit=5)
                config["arquivosRecentes"] = f.path + ";" + cincoMaisRecentes.joinToString()
            }
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

        val resultado = fileChooser.showOpenDialog(this)
        if(resultado == JFileChooser.APPROVE_OPTION) {
            editor.salvarArquivo(File(fileChooser.selectedFile.absolutePath))
        }
    }

    private fun mudarTema(tema: TemaIDE) {
        when(tema) {
            TemaIDE.CLARO -> {
                this.contentPane.background = Color(175, 175, 190)
                this.contentPane.background = Color(75, 75, 85)
            }
            TemaIDE.ESCURO -> {
                this.contentPane.background = Color(75, 75, 85)
                this.contentPane.background = Color(75, 75, 85)
            }
        }
    }

    private fun clicouBotaoAbrirConfiguracoes(e: ActionEvent) {
        val janelaConfiguracoes = JDialog(this, "Configurações")
        janelaConfiguracoes.setIconImage(ImageIcon(getResource("fugue-icons-3.5.6/icons/gear.png")).image)
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

    // Função para ajudar o acesso de recursos
    private fun getResource(resource: String) = javaClass.classLoader.getResource(resource)

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
