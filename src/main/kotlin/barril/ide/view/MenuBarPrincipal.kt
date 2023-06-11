package barril.ide.view

import barril.ide.ResourcesUtil.getIcon
import barril.ide.ResourcesUtil.getResource
import barril.ide.ConfigManager
import java.awt.Color
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class MenuBarPrincipal(controller: FramePrincipal) : JMenuBar() {
   init {
       background = Color(45,45,55)
       adicionarVarios(
           JMenu("Arquivo").apply {
               this.background = Color(65,65,75)
               mnemonic = KeyEvent.VK_A
               adicionarVarios(
                   JMenuItem("Criar Arquivo Vazio").apply {
                       toolTipText = "Abrir nova aba vazia"
                       addActionListener(controller::clicouCriarArquivoVazio)
                   },
                   JMenuItem("Abrir Pasta...").apply {
                       toolTipText = "Abrir pasta no sistema"
                       icon = getIcon("fugue-icons-3.5.6/icons/folder-stand.png")
                       addActionListener(controller::clicouBotaoAbrirPasta)
                   },
                   JMenuItem("Abrir Arquivo...").apply {
                       toolTipText = "Abrir arquivo no sistema"
                       icon = getIcon("fugue-icons-3.5.6/icons/folder-stand.png")
                       addActionListener(controller::clicouBotaoAbrir)
                   },
                   JMenu("Abrir Recentes").also {
                       toolTipText = "Mostrar arquivos abertos recentemente"
                       addMouseListener(object : MouseAdapter() {
                           override fun mouseEntered(e: MouseEvent?) {
                               it.removeAll()
                               val arquivosRecentes = ConfigManager["arquivosRecentes"].split(";")
                               if(arquivosRecentes.isEmpty()) {
                                   it.add(JMenuItem("Nenhum arquivo aberto ainda."))
                                   return
                               }
                               for(i in arquivosRecentes.indices) {
                                   it.add(JMenuItem("${i+1}: ${arquivosRecentes[i]}").apply {
                                       addActionListener {
                                           controller.selecionouArquivoRecenteParaAbrir(arquivosRecentes[i])
                                       }
                                   })
                               }
                           }
                       })
                   },
                   JMenuItem("Salvar").apply {
                       toolTipText = "Salvar arquivo aberto no mesmo lugar onde estava salvo"
                       icon = ImageIcon(getResource("fugue-icons-3.5.6/icons/disk.png"))
                       addActionListener(controller::clicouBotaoSalvar)
                   },
                   JMenuItem("Salvar Como...").apply {
                       toolTipText = "Salvar arquivo aberto em um lugar específico"
                       icon = getIcon("fugue-icons-3.5.6/icons/disks.png")
                       addActionListener(controller::clicouBotaoSalvarComo)
                   }
               )
               addSeparator()
               add(
                   JMenuItem("Sair").apply {
                       toolTipText = "Fechar o programa"
                       icon = getIcon("fugue-icons-3.5.6/icons/door-open-out.png")
                       toolTipText = "Sair da IDE"
                       addActionListener(controller::clicouBotaoSair)
                   }
               )
           },
           JMenu("Ajuda").apply {
               mnemonic = KeyEvent.VK_C
               adicionarVarios(
                   JMenuItem("Abrir config.").apply {
                       toolTipText = "Abrir a configurações"
                       icon = getIcon("fugue-icons-3.5.6/icons/gear.png")
                       addActionListener(controller::clicouBotaoAbrirConfiguracoes)
                   },
                   JMenuItem("Recarregar explorador").apply {
                       toolTipText = "Recarregar os arquivos do explorador de arquivos"
                       icon = getIcon("fugue-icons-3.5.6/icons/arrow-circle.png")
                       addActionListener(controller::clicouRecarregarExplorador)
                   }
               )
           },
           // FIXME
//            JMenu("Janela").apply {
//                mnemonic = KeyEvent.VK_J
//                adicionarVarios(
//                    JMenu("Mudar Tema").apply {
//                        adicionarVarios(
//                            JRadioButtonMenuItem("Tema Escuro").apply {
//                                icon = getIcon("fugue-icons-3.5.6/icons/flag-black.png")
//                                addActionListener { mudarTema(TemaIDE.ESCURO) }
//                            },
//                            JRadioButtonMenuItem("Tema Claro").apply {
//                                icon = getIcon("fugue-icons-3.5.6/icons/flag-white.png")
//                                addActionListener { mudarTema(TemaIDE.CLARO)}
//                            }
//                        )
//                    }
//                )
//            },
       )
   }
}