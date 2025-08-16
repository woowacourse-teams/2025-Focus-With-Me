package com.on.turip.ui.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.on.turip.databinding.BottomSheetFragmentFolderRemoveBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment

class FolderRemoveBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFolderRemoveBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFolderRemoveBinding = BottomSheetFragmentFolderRemoveBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.tvBottomSheetFolderRemoveRemove.setOnClickListener {
            // TODO : 폴더 삭제하는 api 호출
        }
        binding.tvBottomSheetFolderRemoveCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun instance(): FolderRemoveBottomSheetFragment = FolderRemoveBottomSheetFragment()
    }
}
