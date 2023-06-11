package barril.ide.exception

class CaminhoNaoValidoException(caminho: String) : Exception("Caminho inserido (\"$caminho\"não é válido")
