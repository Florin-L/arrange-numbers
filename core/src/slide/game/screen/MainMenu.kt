package slide.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import slide.game.SlideGame

class MainMenu(val game: SlideGame) : Screen {
    private val stage: Stage = Stage(ExtendViewport(game.viewWidth, game.tileHeight))

    init {
        setupUI()
        Gdx.input.inputProcessor = stage
    }

    private fun setupUI() {
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        //
        val skin = Skin()

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 24
        val font = generator.generateFont(parameter)
        generator.dispose()

        skin.add("default", font)

        //
        val style = TextButton.TextButtonStyle()
        style.up = skin.newDrawable("white", Color.DARK_GRAY);
        style.down = skin.newDrawable("white", Color.DARK_GRAY);
        style.checked = skin.newDrawable("white", Color.BLUE);
        style.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        style.font = skin.getFont("default");
        skin.add("default", style);

        val start = TextButton("Start", skin)
        start.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = BoardScreen(game)
            }
        })

        table.row().space(20f).width(200f)
        table.add(start)

        val resume = TextButton("Resume", skin)
        resume.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {

            }
        })

        table.row().space(20f).width(200f)
        table.add(resume)

        val exit = TextButton("Exit", skin)
        exit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        table.row().width(200f)
        table.add(exit)
    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.2f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()

    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }

}