package com.findmyflorist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.findmyflorist.model.Store
import com.findmyflorist.remote.StoresRepository

import android.content.Context
import android.widget.ImageView

import android.widget.TextView
import com.findmyflorist.R


class FavoriteStoreAdapter(private val context: Context) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private val mFavoriteStoresList: ArrayList<Store> = (StoresRepository.getInstance()?.getStoreList?.filter { it.isFavorite } as ArrayList<Store>?)!!
    private lateinit var shopTitle: TextView
    private lateinit var favoriteImageButton: ImageView
    override fun getCount(): Int {
        return mFavoriteStoresList.size
    }

    override fun getItem(position: Int): Any {
        return mFavoriteStoresList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.favorite_item, null)
        }
        shopTitle = convertView!!.findViewById(R.id.shopTitleTextView)
        favoriteImageButton = convertView.findViewById(R.id.favoriteButton)
        shopTitle.text = mFavoriteStoresList[position].storeName
        val store = mFavoriteStoresList[position]
        favoriteImageButton.setOnClickListener {
            if (store.isFavorite) {
                StoresRepository.getInstance()?.addOrRemoveStoreFromFavorite(context, store.storeID,"RemoveStoreFromFavorites")
            }
            val storesList = StoresRepository.getInstance()?.getStoreList
            val storeIndex = (storesList?.indices)?.firstOrNull { i: Int -> storesList[i].storeID== mFavoriteStoresList[position].storeID }
            storeIndex?.let { it1 -> storesList[it1].isFavorite = false }
            mFavoriteStoresList.remove(mFavoriteStoresList[position])
            notifyDataSetChanged()
        }
        return convertView
    }
}