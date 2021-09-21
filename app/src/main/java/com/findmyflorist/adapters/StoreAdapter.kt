package com.findmyflorist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.findmyflorist.fragments.ICommunicator
import com.findmyflorist.R
import com.findmyflorist.model.Store
import com.findmyflorist.databinding.RowBinding
import com.findmyflorist.remote.StoresRepository

class StoreAdapter(private val storesList: ArrayList<Store>, private val context: Context) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RowBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var mCommunicator: ICommunicator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val activity = holder.itemView.context
        holder.mCommunicator = activity as ICommunicator
        val store = storesList[position]
        holder.binding.shopTitleTextView.text = store.storeName
        holder.binding.shopOpenTimeTextView.text = store.isOpen
        holder.binding.distanceTextView.text = store.storeDistanceFromUser.toString()
        if (store.isFavorite) {
            holder.binding.favoriteButton.setImageResource(R.drawable.ic_in_favorite)
        } else {
            holder.binding.favoriteButton.setImageResource(R.drawable.ic_not_in_favorite)
        }
        holder.binding.row.setOnClickListener {
            StoresRepository.getInstance()?.fetchStoreDetails(context, store.storeID)
        }
    }

    override fun getItemCount(): Int {
        return storesList.size
    }
}