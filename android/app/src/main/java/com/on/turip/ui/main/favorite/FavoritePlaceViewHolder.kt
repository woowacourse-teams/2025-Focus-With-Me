package com.on.turip.ui.main.favorite

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceBinding
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel

class FavoritePlaceViewHolder(
    private val binding: ItemFavoritePlaceBinding,
    favoritePlaceListener: FavoritePlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var favoritePlaceModel: FavoritePlaceModel? = null

    init {
        binding.ivFavoritePlaceMapLink.setOnClickListener {
            favoritePlaceModel?.let {
                favoritePlaceListener.onMapClick(it.uri)
            }
        }
        binding.ivFavoritePlaceFavorite.setOnClickListener {
            favoritePlaceModel?.let {
                favoritePlaceListener.onFavoriteClick(it.placeId, it.isFavorite)
            }
        }
    }

    fun bind(favoritePlaceModel: FavoritePlaceModel) {
        this.favoritePlaceModel = favoritePlaceModel

        binding.tvFavoritePlaceCategory.text = favoritePlaceModel.turipCategory
        binding.tvFavoritePlaceName.text = favoritePlaceModel.name
        binding.ivFavoritePlaceFavorite.isSelected = favoritePlaceModel.isFavorite
    }

    companion object {
        fun of(
            parent: ViewGroup,
            favoritePlaceListener: FavoritePlaceListener,
        ): FavoritePlaceViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFavoritePlaceBinding =
                ItemFavoritePlaceBinding.inflate(inflater, parent, false)
            return FavoritePlaceViewHolder(binding, favoritePlaceListener)
        }
    }

    interface FavoritePlaceListener {
        fun onFavoriteClick(
            placeId: Long,
            isFavorite: Boolean,
        )

        fun onMapClick(uri: Uri)
    }
}
