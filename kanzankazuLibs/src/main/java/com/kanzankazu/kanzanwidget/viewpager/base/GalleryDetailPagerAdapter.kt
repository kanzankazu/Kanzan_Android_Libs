package com.kanzankazu.kanzanwidget.viewpager.base

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.kanzankazu.kanzanutil.kanzanextension.view.loadImage
import com.kanzankazu.kanzanutil.kanzanextension.view.loadImageRoundedAll
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.dpToPx

class GalleryDetailPagerAdapter(var mActivity: Activity, var imagePaths: ArrayList<String>, var round: Int, var isNeedZoom: Boolean) : PagerAdapter() {

    override fun getCount(): Int {
        return imagePaths.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val photoView: ImageView = if (isNeedZoom) {
            PhotoView(container.context)
        } else {
            ImageView(container.context)
        }
        if (!isNeedZoom) photoView.scaleType = ImageView.ScaleType.CENTER_CROP
        if (round <= 0) {
            photoView.loadImage(imagePaths[position])
        } else {
            photoView.loadImageRoundedAll(imagePaths[position], round.dpToPx())
        }
        container.addView(photoView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}