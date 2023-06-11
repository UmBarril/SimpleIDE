package barril.ide.view

import javax.swing.JComponent

// Funções Extras para os Views

// Facilitador para não ter que fazer vários adds em sequência
fun JComponent.adicionarVarios(vararg components: JComponent) {
    components.forEach { comp ->
        this.add(comp)
    }
}