package com.findmyflorist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.findmyflorist.adapters.FavoriteStoreAdapter
import com.findmyflorist.databinding.FragmentFavoritesBinding
import com.findmyflorist.model.Store
import com.findmyflorist.remote.StoresRepository

class Favorites : Fragment() {
    private lateinit var mBinding: FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoriteStoreAdapter = context?.let { FavoriteStoreAdapter(it) }
        mBinding.favoriteGridView.adapter = favoriteStoreAdapter
        mBinding.favoriteGridView.setOnItemClickListener { adapter, _, position, _ ->
            val store = adapter.getItemAtPosition(position) as Store
            context?.let { StoresRepository.getInstance()?.fetchStoreDetails(it, store.storeID) }
        }
    }

    companion object
}