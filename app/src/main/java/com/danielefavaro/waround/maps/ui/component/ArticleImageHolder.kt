package com.danielefavaro.waround.maps.ui.component

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.decode.SvgDecoder
import coil.load
import com.danielefavaro.waround.R
import kotlinx.android.synthetic.main.article_image_layout.view.*

class ArticleImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(imageUrl: String) {
        // TODO replace placeholder
        itemView.image.load(imageUrl) {
            decoder(SvgDecoder(itemView.context))
            placeholder(R.drawable.ic_launcher_foreground)
            crossfade(true)
        }
    }
}