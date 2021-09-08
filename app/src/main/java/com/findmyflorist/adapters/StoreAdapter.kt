package com.findmyflorist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.findmyflorist.R
import com.findmyflorist.model.Store

class StoreAdapter(private val storesList: ArrayList<Store>) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var shopTitle: TextView = itemView.findViewById(R.id.shopTitleTextView)
        var shopDistance: TextView = itemView.findViewById(R.id.distanceTextView)
        var isShopOpen: TextView = itemView.findViewById(R.id.shopOpenTimeTextView)
        var shopFavorite: ImageButton = itemView.findViewById(R.id.favoriteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.shopTitle.text = storesList[position].storeName
        holder.isShopOpen.text = storesList[position].isOpen
        holder.shopDistance.text = storesList[position].storeDistanceFromUser.toString()
        if (storesList[position].isFavorite) {
            holder.shopFavorite.setImageResource(R.drawable.ic_in_favorite)
        } else {
            holder.shopFavorite.setImageResource(R.drawable.ic_not_in_favorite)
        }
    }

    override fun getItemCount(): Int {
        return storesList.size
    }
}