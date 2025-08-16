package com.on.turip.ui.folder.model

import androidx.annotation.StringRes
import com.on.turip.R
import com.on.turip.domain.folder.FolderNameStatus

enum class FolderNameStatusModel(
    @StringRes val errorMessage: Int?,
) {
    OK(null),
    EMPTY(null),
    DUPLICATE_NAME(R.string.all_folder_name_error_duplicate),
    DEFAULT_FOLDER_NAME(R.string.all_folder_name_error_default_folder),
    MAX_LENGTH_FOLDER_NAME(R.string.all_folder_name_warning_max_length),
    OUT_OF_BOUND_LENGTH(R.string.all_folder_name_error_out_of_bound),
    ;

    companion object {
        fun of(
            folderName: String,
            originFolderNames: LinkedHashSet<String>,
        ): FolderNameStatusModel {
            val folderNameStatus: FolderNameStatus =
                FolderNameStatus.of(folderName, originFolderNames)
            return when (folderNameStatus) {
                FolderNameStatus.EMPTY -> EMPTY
                FolderNameStatus.MAX_LENGTH_FOLDER_NAME -> MAX_LENGTH_FOLDER_NAME
                FolderNameStatus.OUT_OF_BOUND_LENGTH -> OUT_OF_BOUND_LENGTH
                FolderNameStatus.DEFAULT_FOLDER_NAME -> DEFAULT_FOLDER_NAME
                FolderNameStatus.DUPLICATE_NAME -> DUPLICATE_NAME
                FolderNameStatus.OK -> OK
            }
        }
    }
}
