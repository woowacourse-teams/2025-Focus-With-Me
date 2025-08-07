package com.on.turip.ui.main.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.UsersLikeContent

class UsersLikeContentAdapter(
    private val onContentListener: (content: Content) -> Unit,
) : ListAdapter<UsersLikeContent, UsersLikeContentViewHolder>(UsersLikeContentDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UsersLikeContentViewHolder = UsersLikeContentViewHolder.of(parent, onContentListener)

    override fun onBindViewHolder(
        holder: UsersLikeContentViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object UsersLikeContentDiffUtil : DiffUtil.ItemCallback<UsersLikeContent>() {
        override fun areItemsTheSame(
            oldItem: UsersLikeContent,
            newItem: UsersLikeContent,
        ): Boolean = oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(
            oldItem: UsersLikeContent,
            newItem: UsersLikeContent,
        ): Boolean = oldItem == newItem
    }
}
