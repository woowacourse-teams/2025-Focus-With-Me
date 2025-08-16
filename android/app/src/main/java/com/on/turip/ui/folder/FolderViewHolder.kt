package com.on.turip.ui.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemFolderBinding
import com.on.turip.ui.folder.model.FolderModel

class FolderViewHolder(
    private val binding: ItemFolderBinding,
    folderListener: FolderListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var folderModel: FolderModel? = null

    init {
        binding.ivFolderRemove.setOnClickListener {
            folderModel?.let { folderListener.onRemoveClick(it.id) }
        }

        binding.clFolderItem.setOnClickListener {
            folderModel?.let { folderListener.onItemClick(it.id) }
        }
    }

    fun bind(folderModel: FolderModel) {
        this.folderModel = folderModel

        binding.tvFolderName.text = folderModel.name
        binding.tvFolderPlaceCount.text =
            itemView.context.getString(R.string.all_total_place_count, folderModel.count)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            folderListener: FolderListener,
        ): FolderViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFolderBinding = ItemFolderBinding.inflate(inflater, parent, false)
            return FolderViewHolder(binding, folderListener)
        }
    }

    interface FolderListener {
        fun onRemoveClick(folderId: Long)

        fun onItemClick(folderId: Long)
    }
}
