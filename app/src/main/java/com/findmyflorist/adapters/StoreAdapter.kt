package com.findmyflorist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.textrecognition.view.fragments.ICommunicator
import com.findmyflorist.R
import com.findmyflorist.activities.MainActivity
import com.findmyflorist.model.Store

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.findmyflorist.fragments.StoreDetails


class StoreAdapter(private val storesList: ArrayList<Store>) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var shopTitle: TextView = itemView.findViewById(R.id.shopTitleTextView)
        var shopDistance: TextView = itemView.findViewById(R.id.distanceTextView)
        var isShopOpen: TextView = itemView.findViewById(R.id.shopOpenTimeTextView)
        var shopFavorite: ImageView = itemView.findViewById(R.id.favoriteButton)
        var row : CardView= itemView.findViewById(R.id.row)
        lateinit var mCommunicator : ICommunicator

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, ) {

        val activity = holder.itemView.context
        holder.mCommunicator = activity as ICommunicator
        holder.shopTitle.text = storesList[position].storeName
        holder.isShopOpen.text = storesList[position].isOpen
        holder.shopDistance.text = storesList[position].storeDistanceFromUser.toString()
        if (storesList[position].isFavorite) {
            holder.shopFavorite.setImageResource(R.drawable.ic_in_favorite)
        } else {
            holder.shopFavorite.setImageResource(R.drawable.ic_not_in_favorite)
        }
        holder.row.setOnClickListener {
            holder.mCommunicator.changeFragmentToStoreDetails()
        }
    }

    override fun getItemCount(): Int {
        return storesList.size
    }
}