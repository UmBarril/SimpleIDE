package barril.ide

import javax.swing.ImageIcon

/**
 * Wrapper para facilitar o acesso à pasta de resources
 * @author Slz
 */
object ResourcesUtil {

    /**
     * Pega um recurso da pasta de recursos a partir do caminho dado.
     * @param caminho O caminho do recurso a partir da pasta de resources.
     * @return URL do recurso
     * @author Slz
     * @author Barril
     * @see getIcon
     */
    fun getResource(caminho: String) = javaClass.classLoader.getResource(caminho)

    /**
     * Pega um ImageIcon de um dado caminho.
     * @param caminho O caminho do ícone a partir da pasta de resources.
     * @return o ImageIcon do caminho especificado.
     * @author Slz
     * @see getResource
     */
    fun getIcon(caminho: String) = ImageIcon(getResource(caminho))
}