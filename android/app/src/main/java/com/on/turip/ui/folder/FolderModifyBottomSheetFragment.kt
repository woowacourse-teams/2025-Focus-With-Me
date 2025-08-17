package com.on.turip.ui.folder

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.on.turip.databinding.BottomSheetFragmentFolderModifyBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.folder.model.FolderNameStatusModel

class FolderModifyBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFolderModifyBinding>() {
    private val viewModel: FolderModifyBottomSheetViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFolderModifyBinding = BottomSheetFragmentFolderModifyBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupEditText()
        setupObservers()
        setupListeners()
    }

    private fun setupEditText() {
        binding.etBottomSheetFolderModifyFolderName.apply {
            requestFocus()
            post {
                val inputMethodManager: InputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
            filters = arrayOf(InputFilter.LengthFilter(20))
        }
    }

    private fun setupObservers() {
        viewModel.folderNameStatus.observe(this) { folderNameStatusModel: FolderNameStatusModel ->
            binding.tvBottomSheetFolderModifyConfirm.isEnabled =
                (folderNameStatusModel == FolderNameStatusModel.OK || folderNameStatusModel == FolderNameStatusModel.MAX_LENGTH_FOLDER_NAME)

            folderNameStatusModel.errorMessage?.let {
                binding.tvBottomSheetFolderModifyError.apply {
                    visibility = View.VISIBLE
                    setText(it)
                }
            } ?: run {
                binding.tvBottomSheetFolderModifyError.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.tvBottomSheetFolderModifyConfirm.setOnClickListener {
            // TODO : 폴더 수정 api 호출 필요
            dismiss()
        }
        binding.etBottomSheetFolderModifyFolderName.addTextChangedListener { text: Editable? ->
            viewModel.updateFolderName(text.toString())
        }
    }

    companion object {
        fun instance(): FolderModifyBottomSheetFragment = FolderModifyBottomSheetFragment()
    }
}
