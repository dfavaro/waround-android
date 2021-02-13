package com.danielefavaro.waround.maps.ui.component

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RouteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(value: String) {
        val htmlValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(value)
        }
        (itemView as TextView).apply {
            text = htmlValue
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextColor(
                    itemView.resources.getColor(
                        android.R.color.black,
                        itemView.context.theme
                    )
                )
            } else {
                setTextColor(itemView.resources.getColor(android.R.color.black))
            }
        }
    }

}