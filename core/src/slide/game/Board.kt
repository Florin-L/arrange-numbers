package slide.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import slide.game.screen.BoardScreen
import java.util.*

/**
 * @class Dimensions
 *
 * Stores the dimensions (rows and columns) of a board.
 */
data class Dimensions(val rows: Int, val columns: Int)

/**
 * @class Size
 *
 * Stores the board size in pixels (width and height).
 */
data class SizeT<T>(val width: T, val height: T)

typealias Size = SizeT<Float>

/**
 * @class Coordinates
 *
 * Stores the coordinate of a tile from a board.
 */
data class Coordinates(val row: Int, val column: Int)

/**
 *
 */
enum class Direction(val dx: Int, val dy: Int) {
    Up(0, -1),
    Left(-1, 0),
    Down(0, 1),
    Right(1, 0)
}

/**
 * @class Tile
 *
 * Represents a tile from the board. The tile keeps the number to be rendered.
 *
 * @param number The number to be rendered.
 * @param coords The coordinates (row and column) of the tile.
 * @param size The sizes (width and height) of the board in pixels.
 * @param parent The container (board) which the tile is rendered onto.
 */
class Tile(internal var number: Int, internal val coords: Coordinates,
           size: Size, private val parent: Board) {

    val bounds = Rectangle()
    private var sprite: Sprite

    internal val isEmpty: Boolean
        get() = number == -1

    init {
        // flip the row/Y coordinate
        val y = parent.y + parent.height - size.height
        bounds.set(parent.x + coords.column * size.width,
                y - coords.row * size.height,
                size.width, size.height)

        sprite = createSprite(size)
        sprite.setBounds(bounds.x, bounds.y, bounds.width, bounds.height)
    }

    /**
     * Render this tile.
     *
     * @param batch The batch where the tile is rendered in.
     */
    fun render(batch: Batch?) {
        sprite.draw(batch)

        if (number != -1) {
            drawText(batch)
        }
    }

    fun dispose() {}

    private fun createSprite(size: Size): Sprite {
        val pixmap = Pixmap(size.width.toInt(), size.height.toInt(), Pixmap.Format.RGBA8888)

        pixmap.setColor(Color.OLIVE)
        pixmap.fillRectangle(0, 0, size.width.toInt(), size.height.toInt())

        pixmap.setColor(Color.YELLOW)
        pixmap.drawRectangle(0, 0, size.width.toInt(), size.height.toInt())

        val sprite = Sprite(Texture(pixmap))
        pixmap.dispose()

        return sprite
    }

    private fun drawText(batch: Batch?) {
        val text = number.toString()
        val layout = GlyphLayout(parent.font, text)

        val textX = bounds.x + (bounds.width - layout.width) / 2
        val textY = bounds.y + (bounds.height + layout.height) / 2

        parent.font.draw(batch, layout, textX, textY)
    }
}

/**
 * @class Board
 *
 * Stores and displays the tiles and also handles the user input.
 */
class Board(private val dimension: Dimensions,
            private val tileSize: Size,
            parentBounds: Size, val screen: BoardScreen) : Actor() {

    private var tiles: Array<Tile>
    internal var font: BitmapFont
    private lateinit var background: Sprite

    private var gameEnded = false
        set(value) {
            field = value
            screen.overIsVisible = value
        }

    init {

        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 28
        parameter.flip = false
        font = generator.generateFont(parameter)
        generator.dispose()

        val w = dimension.columns * tileSize.width
        val h = dimension.rows * tileSize.height

        setBounds((parentBounds.width - w) / 2, (parentBounds.height - h) / 2, w, h)
        createBackground()

        tiles = Array(dimension.rows * dimension.columns) { i ->
            val n = if (i < dimension.rows * dimension.columns - 1) i + 1 else -1
            Tile(n, Coordinates(i / dimension.columns, i % dimension.columns), tileSize, this)
        }
        shuffle()

        touchable = Touchable.enabled

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (gameEnded) {
                    return true
                }

                event?.let {
                    onTouch(event.stageX, event.stageY)
                }
                return true
            }
        })
    }

    /**
     * Handles the touch event.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    private fun onTouch(x: Float, y: Float) {
        val pos = stageToLocalCoordinates(Vector2(x, y))

        // flip the Y-coordinates
        val coords = screenToBoard(pos.x.toInt(), (height - pos.y).toInt())

        move(coords) {
            onMoved()
        }
    }

    /**
     * Shuffles the array of tiles.
     */
    fun shuffle() {
        val rnd = Random()
        for (i in tiles.size - 2 downTo 1) {
            val j = rnd.nextInt(i + 1)

            tiles[i].number = tiles[j].number.also {
                tiles[j].number = tiles[i].number
            }
        }

        val i = rnd.nextInt(tiles.size - 1)

        tiles[i].number = tiles[tiles.size - 1].number.also {
            tiles[tiles.size - 1].number = tiles[i].number
        }

        gameEnded = false
    }

    private fun createBackground() {
        val pixmap = Pixmap(width.toInt(), height.toInt(),
                Pixmap.Format.RGBA8888)

        pixmap.setColor(Color.LIME)
        pixmap.fillRectangle(0, 0, width.toInt(), height.toInt())

        background = Sprite(Texture(pixmap))
        pixmap.dispose()
    }

    /**
     * Renders the board and the tiles.
     *
     * @param batch The sprite batch.
     * @param parentAlpha
     */
    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(background, x, y)

        for (tile in tiles) {
            tile.render(batch)
        }
    }

    /**
     * Checks if the numbers were arranged in a ascending order.
     */
    private fun checkEndGame(): Boolean {
        for (i in 0 until tiles.size - 2) {
            if (tiles[i + 1].number != tiles[i].number + 1) {
                return false
            }
        }
        return true
    }

    /**
     * Checks if the given grid coordinates are inside the board.
     */
    private fun isValidCoord(coord: Coordinates) = (coord.row >= 0) && (coord.row < dimension.rows) &&
            (coord.column >= 0) && (coord.column < dimension.columns)

    /**
     * Try to move the tiles over the empty position starting from a given board coordinates.
     * All the directions are considering for the search.
     *
     * @param source The starting position.
     * @return true if the tiles can be moved, false otherwise.
     */
    private fun move(source: Coordinates, done: (() -> Unit)?) {
        for (direction in Direction.values()) {

            val coords = findToMove(source, direction)

            if (!coords.isEmpty() && (coords.size >= 2)) {
                for (i in coords.size - 1 downTo 1) {
                    tiles[index(coords[i])].number = tiles[index(coords[i - 1])].number.also {
                        tiles[index(coords[i - 1])].number = tiles[index(coords[i])].number
                    }
                }

                if (done != null) {
                    done()
                }

                return
            }
        }
    }

    /**
     * Finds the tile which might be moved starting from a given board coordinates.
     *
     * @param source The boars coordinates
     * @param direction The direction (up/left/down/right) the tiles are searched for
     *
     * @return
     */
    private fun findToMove(source: Coordinates, direction: Direction): List<Coordinates> {
        val coords = mutableListOf<Coordinates>()

        var current = source
        while (isValidCoord(current) && !tiles[index(current)].isEmpty) {
            coords.add(current)
            current = Coordinates(current.row + direction.dy,
                    current.column + direction.dx)
        }

        if (isValidCoord(current)) {
            coords.add(current)
        }

        return if (isValidCoord(current)) coords else listOf()
    }

    /**
     * This function is called after the tiles have been moved.
     */
    private fun onMoved() {
        Gdx.graphics.requestRendering()

        if (checkEndGame()) {
            gameEnded = true
            Gdx.graphics.setTitle("Arrange numbers - You win !")
        }
    }

    /**
     * The callee should check that the screen coordinates are within the bounds of the bounds.
     */
    private fun screenToBoard(x: Int, y: Int): Coordinates {
        return Coordinates((y / tileSize.height).toInt(), (x / tileSize.width).toInt())
    }

    /**
     * Converts a grid coordinate to the index from the array of tiles.
     */
    private fun index(coords: Coordinates): Int = coords.row * dimension.rows + coords.column

    fun dispose() {
        tiles.forEach { tile -> tile.dispose() }
    }

    private fun dumpTiles() {
        for (t in tiles) {
            println("${t.coords}, ${t.bounds}, ${t.number}")
        }
    }
}
