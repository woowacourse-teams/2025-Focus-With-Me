package com.on.turip.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemUsersLikeContentBinding
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.loadRoundedCornerImage

class UsersLikeContentViewHolder(
    private val binding: ItemUsersLikeContentBinding,
    onContentListener: OnContentListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var content: Content? = null

    init {
        itemView.setOnClickListener {
            content?.let { onContentListener.onContentClick(it) }
        }
    }

    fun bind(usersLikeContent: UsersLikeContent) {
        this.content = usersLikeContent.content
        binding.apply {
            tvUsersLikeContentTitle.text = usersLikeContent.content.videoData.title
            tvUsersLikeContentRegion.text = usersLikeContent.content.city.name
            tvUsersLikeContentChannelName.text = usersLikeContent.content.creator.channelName
            ivUsersLikeContentThumbnail.loadRoundedCornerImage(
                imageUrl = TuripUrlConverter.convertVideoThumbnailUrl(usersLikeContent.content.videoData.url),
                radius = 12,
            )
            tvUsersLikeContentDescription.text =
                itemView.context.getString(
                    R.string.all_video_description,
                    usersLikeContent.content.videoData.uploadedDate,
                    itemView.context.getString(
                        R.string.all_travel_duration,
                        usersLikeContent.tripDuration.nights,
                        usersLikeContent.tripDuration.days,
                    ),
                )
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onContentListener: OnContentListener,
        ): UsersLikeContentViewHolder {
            val binding: ItemUsersLikeContentBinding =
                ItemUsersLikeContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                )
            return UsersLikeContentViewHolder(binding, onContentListener)
        }
    }

    fun interface OnContentListener {
        fun onContentClick(content: Content)
    }
}
