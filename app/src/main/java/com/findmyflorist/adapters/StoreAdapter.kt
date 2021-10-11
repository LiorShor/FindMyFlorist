package com.findmyflorist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.findmyflorist.fragments.ICommunicator
import com.findmyflorist.R
import com.findmyflorist.activities.MainActivity
import com.findmyflorist.model.Store
import com.findmyflorist.databinding.RowBinding
import com.findmyflorist.fragments.IAdapterListener
import com.findmyflorist.remote.StoresRepository

class StoreAdapter(private val storesList: ArrayList<Store>, private val context: Context) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>(), IAdapterListener {
    inner class ViewHolder(val binding: RowBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var mCommunicator: ICommunicator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        setAdapterListener()
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = holder.itemView.context
        holder.mCommunicator = activity as ICommunicator
        val store = storesList[position]
        holder.binding.shopTitleTextView.text = store.storeName
        if(store.isOpen == "true") {
            holder.binding.shopOpenTimeTextView.text = context.getString(R.string.open)
        }
        else{
            holder.binding.shopOpenTimeTextView.text = context.getString(R.string.closed)
        }
        holder.binding.distanceTextView.text = "${store.storeDistanceFromUser.toString()} Km"
        if(MainActivity.user.fullName == "Hello guest"){
            holder.binding.favoriteButton.isEnabled = false
        }
        else {
            holder.binding.favoriteButton.isEnabled = true
            if (store.isFavorite) {
                holder.binding.favoriteButton.setImageResource(R.drawable.ic_in_favorite)
            } else {
                holder.binding.favoriteButton.setImageResource(R.drawable.ic_not_in_favorite)
            }
        }
        holder.binding.row.setOnClickListener {
            StoresRepository.getInstance()?.fetchStoreDetails(context, store.storeID)
        }
        holder.binding.favoriteButton.setOnClickListener {

            if (store.isFavorite) {
                StoresRepository.getInstance()?.addOrRemoveStoreFromFavorite(context, store.storeID,"RemoveStoreFromFavorites")
                holder.binding.favoriteButton.setImageResource(R.drawable.ic_not_in_favorite)
            } else {
                StoresRepository.getInstance()?.addOrRemoveStoreFromFavorite(context, store.storeID,"AddStoreToFavorites")
                holder.binding.favoriteButton.setImageResource(R.drawable.ic_in_favorite)
            }
            storesList[position].isFavorite = !storesList[position].isFavorite
        }
    }

    override fun getItemCount(): Int {
        return storesList.size
    }

    private fun setAdapterListener() {
        StoresRepository.getInstance()?.setAdapter(this)
    }

    override fun refreshAdapter() {
        notifyDataSetChanged()
    }
}