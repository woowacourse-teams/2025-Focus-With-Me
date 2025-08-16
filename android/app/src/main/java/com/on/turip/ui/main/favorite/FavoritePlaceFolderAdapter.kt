package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderAdapter(
    private val onPlaceFolderListener: FavoritePlaceFolderViewHolder.OnPlaceFolderListener,
) : ListAdapter<FavoritePlaceFolderModel, FavoritePlaceFolderViewHolder>(FavoritePlaceFolderDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoritePlaceFolderViewHolder = FavoritePlaceFolderViewHolder.of(parent, onPlaceFolderListener)

    override fun onBindViewHolder(
        holder: FavoritePlaceFolderViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FavoritePlaceFolderDiffUtil : DiffUtil.ItemCallback<FavoritePlaceFolderModel>() {
        override fun areItemsTheSame(
            oldItem: FavoritePlaceFolderModel,
            newItem: FavoritePlaceFolderModel,
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: FavoritePlaceFolderModel,
            newItem: FavoritePlaceFolderModel,
        ): Boolean = oldItem == newItem
    }
}
