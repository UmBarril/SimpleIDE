package barril.ide.exception

class ConfigNaoInicializadaException :
    Exception("Configuração ainda não inicializada. Antes de começar, rode Config.inicializar().")
