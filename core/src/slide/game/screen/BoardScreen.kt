package slide.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import slide.game.Board
import slide.game.Dimensions
import slide.game.Size
import slide.game.SlideGame

class BoardScreen(val game: SlideGame) : Screen {

    private val camera = OrthographicCamera()
    private val board = Board(Dimensions(game.rows, game.columns),
            Size(game.tileWidth, game.tileHeight),
            Size(game.viewWidth, game.viewHeight),
            this)

    private val stage = Stage(FitViewport(game.viewWidth, game.viewHeight))

    var winText: Label? = null

    var overIsVisible: Boolean = false
        set(value) {
            field = value
            winText?.isVisible = value
        }

    init {
        camera.setToOrtho(true, game.viewWidth, game.viewHeight)
        setupUI()
    }

    private fun setupUI() {
        stage.addActor(board)

        //
        val skin = Skin()

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 26
        val font = generator.generateFont(parameter)
        generator.dispose()

        skin.add("default", font)

        val labelStyle = Label.LabelStyle()
        labelStyle.font = skin.getFont("default")
        labelStyle.background = skin.newDrawable("white", Color.OLIVE)
        skin.add("label", labelStyle)

        winText = Label("Won !", labelStyle)
        var layout = GlyphLayout(font, winText?.text)
        winText?.setPosition(board.x + board.width + 10f, board.y + board.height - layout.height, Align.left)
        winText?.width = 95f
        winText?.setAlignment(Align.center)
        winText?.isVisible = false
        stage.addActor(winText)

        //
        val style = TextButton.TextButtonStyle()
        style.up = skin.newDrawable("white", Color.OLIVE)
        style.down = skin.newDrawable("white", Color.OLIVE)
        style.checked = skin.newDrawable("white", Color.OLIVE)
        style.over = skin.newDrawable("white", Color.LIGHT_GRAY)
        style.font = skin.getFont("default")
        skin.add("default", style)

        val shuffle = TextButton("Shuffle", skin)
        shuffle.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                board.shuffle()
            }
        })

        layout = GlyphLayout(font, shuffle.text)
        shuffle.setPosition(board.x - 100f, board.y + board.height - layout.height, Align.left)
        shuffle.width = 95f
        stage.addActor(shuffle)

        val exit = TextButton("Exit", skin)
        exit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        layout = GlyphLayout(font, exit.text)
        exit.setPosition(board.x - 100f, board.y + layout.height, Align.left)
        exit.width = 95f

        stage.addActor(exit)

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        stage.batch.projectionMatrix = stage.camera.combined
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        board.dispose()
        stage.dispose()
    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

}