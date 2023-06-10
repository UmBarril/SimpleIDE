package IDE

import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JPanel
import javax.swing.JViewport
import javax.swing.Scrollable

internal class ScrollablePanel : JPanel(), Scrollable {
    //the panel prefers to take as much height as possible
    override fun getPreferredScrollableViewportSize() = Dimension(preferredSize.width, Int.MAX_VALUE)
    override fun getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = 1
    override fun getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int) = 1
    override fun getScrollableTracksViewportWidth() = true
    override fun getScrollableTracksViewportHeight() = (parent as JViewport).height > preferredSize.height
}
