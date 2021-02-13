package com.danielefavaro.waround.maps.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielefavaro.waround.maps.ui.component.RouteHolder

class RouteAdapter(
    private val route: MutableList<String> = mutableListOf()
) : RecyclerView.Adapter<RouteHolder>() {

    override fun getItemCount() = route.size

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RouteHolder(
        LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
    )

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        holder.bind(route[position])
    }

    fun setRoute(route: List<String>) {
        val size = this.route.size
        this.route.clear()
        notifyItemRangeRemoved(0, size)

        this.route.addAll(route)
        notifyItemRangeInserted(0, itemCount)
    }
}