package com.on.turip.ui.main.favorite

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.FragmentFavoritePlaceBinding
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFragment : BaseFragment<FragmentFavoritePlaceBinding>() {
    private val folderAdapter: FavoritePlaceFolderAdapter by lazy {
        FavoritePlaceFolderAdapter { favoritePlaceFolderModel: FavoritePlaceFolderModel ->
            // TODO: viewModel에서 선택한 찜 폴더에 해당하는 아이템을 불러오기
        }
    }
    private val placeAdapter: FavoritePlaceAdapter by lazy {
        FavoritePlaceAdapter(
            object : FavoritePlaceViewHolder.FavoritePlaceListener {
                override fun onFavoriteClick(
                    placeId: Long,
                    isFavorite: Boolean,
                ) {
                    // TODO: 장소 찜 클릭 시 로직
                }

                override fun onMapClick(uri: Uri) {
                    // TODO: 지도 버튼 클릭 시 로직
                }
            },
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoritePlaceBinding = FragmentFavoritePlaceBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
    }

    private fun setupAdapters() {
        binding.rvFavoritePlaceFolder.apply {
            adapter = folderAdapter
            addOnItemTouchListener(RecyclerViewTouchInterceptor)
        }
        binding.rvFavoritePlacePlace.adapter = placeAdapter
    }

    companion object {
        fun instance(): FavoritePlaceFragment = FavoritePlaceFragment()
    }
}

private object RecyclerViewTouchInterceptor : RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(
        recyclerView: RecyclerView,
        motionEvent: MotionEvent,
    ): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }

    override fun onTouchEvent(
        recyclerView: RecyclerView,
        motionEvent: MotionEvent,
    ) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
