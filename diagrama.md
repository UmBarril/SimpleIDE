classDiagram
direction BT
class ArquivoNode {
  + ArquivoNode(File) 
  + carregarTodosOsSubArquivos(DefaultTreeModel) Unit
   File arquivo
}
class CaminhoNaoValidoException {
  + CaminhoNaoValidoException(String) 
}
class ConfigDialog {
  + ConfigDialog(Frame, Dimension, () -~ Unit) 
  + abrir() Unit
}
class ConfigManager {
  + set(String, String) Object?
  + salvar() Unit
  + get(String) String
  + inicializar() Unit
   Boolean inicializado
}
class ConfigNaoInicializadaException {
  + ConfigNaoInicializadaException() 
}
class EditorDeTextoComAbasPanel {
  + EditorDeTextoComAbasPanel(Dimension) 
  + atualizarTamanhoTexto() Unit
  - abrirAquivoSemVerificacao(File) Unit
  - abrirAbaESelecionar(String, String, JComponent) Unit
  + criarAbaVazia(String) Unit
  + criarAbaVaziaComPopup() Unit
  + salvarArquivo(String) Unit
  + salvarArquivo(File) Unit
  + abrirArquivo(File) Unit
  + abrirArquivo(String) Unit
  + criarTelaInicial() JPanel
   String? nomeDoArquivoAberto
   String? caminhoDoArquivoAberto
   Int quantidadeDeAbas
}
class EditorDeTextoScroll {
  + EditorDeTextoScroll() 
  + EditorDeTextoScroll(String, String?, Boolean) 
  - atualizarContadorDeLinhas() Unit
  - criarContadorDeLinhas() JTextPane
  - criarAreaDeEscrita(String) JTextPane
  + atualizarTamanhoTexto() Unit
   String? textoSelecionado
   String conteudo
   String? caminhoDoArquivo
   Int tamanhoDaFonte
}
class ExploradorDeArquivosPanel {
  + ExploradorDeArquivosPanel(File, Dimension) 
  + ExploradorDeArquivosPanel(String, Dimension) 
  + adicionarArquivoSelecionadoListener((File) -~ Unit) Unit
  + recarregarArvore() Unit
}
class FramePrincipal {
  + FramePrincipal(Dimension) 
  + clicouBotaoSair(ActionEvent?) Unit
  + clicouBotaoSalvar(ActionEvent) Unit
  + clicouBotaoAbrirConfiguracoes(ActionEvent) Unit
  + fecharProgramaSeConfirmar() Unit
  + clicouBotaoSalvarComo(ActionEvent?) Unit
  + selecionouArquivoRecenteParaAbrir(String) Unit
  + clicouBotaoAbrir(ActionEvent) Unit
  + clicouCriarArquivoVazio(ActionEvent?) Unit
  + clicouBotaoAbrirPasta(ActionEvent) Unit
  + clicouRecarregarExplorador(ActionEvent?) Unit
}
class IMenuBarPrincipalController {
<<Interface>>
  + clicouBotaoAbrir(ActionEvent) Unit
  + clicouBotaoSair(ActionEvent?) Unit
  + selecionouArquivoRecenteParaAbrir(String) Unit
  + clicouBotaoSalvarComo(ActionEvent?) Unit
  + clicouBotaoAbrirPasta(ActionEvent) Unit
  + clicouBotaoSalvar(ActionEvent) Unit
  + clicouBotaoAbrirConfiguracoes(ActionEvent) Unit
}
class MainKt {
  + main() Unit
}
class MenuBarPrincipal {
  + MenuBarPrincipal(FramePrincipal) 
}
class RenderizadorDeNodes {
  + RenderizadorDeNodes() 
  + getTreeCellRendererComponent(JTree?, Object?, Boolean, Boolean, Boolean, Int, Boolean) Component
   Color foreground
   Color backgroundSelectionColor
   Color backgroundNonSelectionColor
}
class ResourcesUtil {
  + getIcon(String) ImageIcon
  + getResource(String) URL
}
class ScrollablePanel {
  + ScrollablePanel() 
  + getScrollableUnitIncrement(Rectangle, Int, Int) Int
  + getScrollableBlockIncrement(Rectangle, Int, Int) Int
   Dimension preferredScrollableViewportSize
   Boolean scrollableTracksViewportHeight
   Boolean scrollableTracksViewportWidth
}
class Terminalntegrado {
  + Terminalntegrado(String) 
}
class ViewExtensionsKt {
  + adicionarVarios(JComponent, JComponent[]) Unit
}

ExploradorDeArquivosPanel  -->  ArquivoNode 
EditorDeTextoComAbasPanel  ..>  EditorDeTextoScroll : «create»
EditorDeTextoScroll  ..>  ScrollablePanel : «create»
ExploradorDeArquivosPanel  ..>  ArquivoNode : «create»
ExploradorDeArquivosPanel  ..>  CaminhoNaoValidoException : «create»
ExploradorDeArquivosPanel  ..>  RenderizadorDeNodes : «create»
ExploradorDeArquivosPanel  ..>  ScrollablePanel : «create»
FramePrincipal  ..>  ConfigDialog : «create»
FramePrincipal  ..>  ConfigNaoInicializadaException : «create»
FramePrincipal  ..>  EditorDeTextoComAbasPanel : «create»
FramePrincipal "1" *--> "editor 1" EditorDeTextoComAbasPanel 
FramePrincipal "1" *--> "explorador 1" ExploradorDeArquivosPanel 
FramePrincipal  ..>  ExploradorDeArquivosPanel : «create»
FramePrincipal  ..>  MenuBarPrincipal : «create»
MainKt  ..>  FramePrincipal : «create»
ExploradorDeArquivosPanel  -->  RenderizadorDeNodes 
