package com.on.turip.ui.folder

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.folder.model.FolderModel

class FolderAdapter(
    private val folderListener: FolderViewHolder.FolderListener,
) : ListAdapter<FolderModel, FolderViewHolder>(FolderDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FolderViewHolder = FolderViewHolder.of(parent, folderListener)

    override fun onBindViewHolder(
        holder: FolderViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FolderDiffUtil : DiffUtil.ItemCallback<FolderModel>() {
        override fun areItemsTheSame(
            oldItem: FolderModel,
            newItem: FolderModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: FolderModel,
            newItem: FolderModel,
        ): Boolean = oldItem == newItem
    }
}
