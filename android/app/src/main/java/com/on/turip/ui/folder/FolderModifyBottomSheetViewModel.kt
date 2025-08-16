package com.on.turip.ui.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.on.turip.ui.folder.model.FolderNameStatusModel

class FolderModifyBottomSheetViewModel : ViewModel() {
    private val _inputFolderName: MutableLiveData<String> = MutableLiveData("")
    val inputFolderName: LiveData<String> get() = _inputFolderName

    val folderNameStatus: LiveData<FolderNameStatusModel> =
        inputFolderName.map { FolderNameStatusModel.of(it, LinkedHashSet()) }

    fun updateFolderName(input: String) {
        _inputFolderName.value = input
    }
}
