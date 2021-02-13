package com.danielefavaro.waround.maps.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielefavaro.waround.R
import com.danielefavaro.waround.maps.ui.component.ArticleImageHolder

class ArticleImageAdapter(
    val imageUrls: MutableList<String> = mutableListOf()
) : RecyclerView.Adapter<ArticleImageHolder>() {

    override fun getItemCount(): Int = imageUrls.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ArticleImageHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.article_image_layout, parent, false)
    )

    override fun onBindViewHolder(holder: ArticleImageHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    fun setImages(imageUrls: List<String>) {
        val size = this.imageUrls.size
        this.imageUrls.clear()
        notifyItemRangeRemoved(0, size)

        this.imageUrls.addAll(imageUrls)
        notifyItemRangeInserted(0, itemCount)
    }
}