package slide.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import slide.game.screen.BoardScreen

class SlideGame : Game() {

    internal lateinit var font: BitmapFont

    // the view size
    val viewWidth: Float = 600.0f
    val viewHeight: Float = 480.0f

    // the board dimensions
    val rows = 4
    val columns = 4

    // the tile dimensions
    val tileWidth = 100f
    val tileHeight = 100f


    override fun create() {
        font = BitmapFont()

        Gdx.graphics.setTitle("Arrange numbers")
        Gdx.graphics.isContinuousRendering = false

        screen = BoardScreen(this)
    }

    override fun dispose() {
        screen.dispose()
        font.dispose()
    }
}
