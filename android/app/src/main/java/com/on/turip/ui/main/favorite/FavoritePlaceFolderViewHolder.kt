package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemPlaceFolderBinding
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderViewHolder(
    private val binding: ItemPlaceFolderBinding,
    onPlaceFolderListener: OnPlaceFolderListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var favoritePlaceFolderModel: FavoritePlaceFolderModel? = null

    init {
        itemView.setOnClickListener {
            favoritePlaceFolderModel?.let { onPlaceFolderListener.onPlaceFolderClick(it) }
        }
    }

    fun bind(favoritePlaceFolderModel: FavoritePlaceFolderModel) {
        this.favoritePlaceFolderModel = favoritePlaceFolderModel
        binding.tvPlaceFolder.text = favoritePlaceFolderModel.name
        itemView.isSelected = favoritePlaceFolderModel.isSelected
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onPlaceFolderListener: OnPlaceFolderListener,
        ): FavoritePlaceFolderViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemPlaceFolderBinding =
                ItemPlaceFolderBinding.inflate(inflater, parent, false)
            return FavoritePlaceFolderViewHolder(binding, onPlaceFolderListener)
        }
    }

    fun interface OnPlaceFolderListener {
        fun onPlaceFolderClick(favoritePlaceFolderModel: FavoritePlaceFolderModel)
    }
}
