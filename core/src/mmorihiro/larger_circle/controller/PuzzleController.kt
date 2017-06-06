package mmorihiro.larger_circle.controller

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import ktx.actors.alpha
import ktx.actors.minus
import ktx.actors.plus
import ktx.actors.then
import mmorihiro.larger_circle.view.PuzzleView


class PuzzleController(
        val onHit: (Int, Pair<Int, Int>, () -> Unit) -> Unit) : Controller {
    override val view = PuzzleView(
            ::touchAction,
            ::onTouchDragged,
            { view -> onTouchUp(view, this::showAction) }).apply {
        this + backGround
        this + puzzleBackGround
        this + bubbleGroup
        this + bar
        this + cover
        cover + (delay(3.8f) then Actions.run { this - cover })
        bubbles.forEachIndexed { index, row ->
            row.forEach { bubble ->
                bubbleGroup += bubble
                if (index != 4) {
                    bubble + (delay(index * 0.9f)
                            then moveBy(0f, -tileSize * 4f, 0.9f,
                            Interpolation.bounceOut)) +
                            action {
                                if (bubble.y <= tileSize * 4) {
                                    bubble.alpha = 1f
                                    true
                                } else false
                            }
                }
            }
        }
    }

    private fun showAction(view: PuzzleView,
                           type: Pair<Int, Int>, size: Int): Unit = view.run {
        cover.alpha = 0f
        this + cover
        cover + (delay(0.2f) then fadeIn(0.2f) then Actions.run {
            resetBubbles()
            val label = createLabel(size)
            val bubble = createBubble(type)
            bubble + (fadeAction() then Actions.run {
                this - bubble
            })
            label + (fadeAction() then Actions.run {
                this - label
                onHit(size, type, this@PuzzleController::resume)
            })
            this + bubble
            this + label
        })
    }

    private fun fadeAction() =
            delay(0.7f) then parallel(fadeOut(0.3f), moveBy(0f, 10f, 0.3f))

    fun resume() {
        view - view.cover
    }
}