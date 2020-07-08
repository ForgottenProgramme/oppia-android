package org.oppia.util.image

import android.graphics.RectF
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoViewAttacher
import org.oppia.app.model.ImageWithRegions.LabeledRegion
import kotlin.math.roundToInt

class ClickableAreasImage(
  private val attacher: PhotoViewAttacher,
  private val overlayView: View,
  private val listener: OnClickableAreaClickedListener
) : OnPhotoTapListener {
  private var clickableAreas: List<LabeledRegion> = emptyList()

  init {
    attacher.setOnPhotoTapListener(this)
  }

  /**
   * Called when an image is clicked.
   *
   * @param view the original view on which the tap/click occurs.
   * @param x the relative x coordinate according to image
   * @param y the relative y coordinate according to image
   */
  override fun onPhotoTap(view: ImageView, x: Float, y: Float) {
    val clickableArea = getClickAbleAreas(x, y)
    if (clickableArea.hasRegion()) {
      val imageRect = RectF(
        getXCoordinate(clickableArea.region.area.lowerRight.x),
        getYCoordinate(clickableArea.region.area.lowerRight.y),
        getXCoordinate(clickableArea.region.area.upperLeft.x),
        getYCoordinate(clickableArea.region.area.upperLeft.y)
      )
      val overlayViewParams = FrameLayout.LayoutParams(
        imageRect.width().roundToInt(),
        imageRect.height().roundToInt()
      )
      overlayView.apply {
        this.x = imageRect.left
        this.y = imageRect.top
        layoutParams = overlayViewParams
        visibility = View.GONE
      }
      listener.onClickableAreaTouched(clickableArea.label)
    } else {
      overlayView.visibility = View.GONE
    }
  }

  private fun getClickAbleAreas(x: Float, y: Float): LabeledRegion {
    for (ca in clickableAreas) {
      if (isBetween(ca.region.area.lowerRight.x, ca.region.area.upperLeft.x, x)) {
        if (isBetween(ca.region.area.lowerRight.y, ca.region.area.upperLeft.y, y)) {
          return ca
        }
      }
    }
    return LabeledRegion.getDefaultInstance()
  }

  /* Return whether a point lies between two points.*/
  private fun isBetween(start: Float, end: Float, actual: Float): Boolean {
    return actual in start..end
  }

  /* Get X co-ordinate scaled according to image.*/
  private fun getXCoordinate(x: Float): Float {
    val rect = attacher.displayRect
    return (x * rect.width()) + rect.left
  }

  /* Get Y co-ordinate scaled according to image.*/
  private fun getYCoordinate(x: Float): Float {
    val rect = attacher.displayRect
    return (x * rect.height()) + rect.top
  }

  fun setClickableAreas(clickableAreas: List<LabeledRegion>) {
    this.clickableAreas = clickableAreas
  }

}