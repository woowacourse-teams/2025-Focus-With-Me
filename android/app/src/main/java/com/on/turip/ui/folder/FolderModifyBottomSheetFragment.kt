package com.on.turip.ui.folder

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.on.turip.databinding.BottomSheetFragmentFolderModifyBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.folder.model.FolderNameStatusModel

class FolderModifyBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFolderModifyBinding>() {
    private val viewModel: FolderModifyBottomSheetViewModel by viewModels()

    private val folderNameTextWatcher: TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {
                viewModel.updateFolderName(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

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
            addTextChangedListener(folderNameTextWatcher)
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
    }
}
