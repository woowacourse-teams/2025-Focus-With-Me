package com.on.turip.domain.folder

enum class FolderNameStatus {
    OK,
    EMPTY,
    DUPLICATE_NAME,
    DEFAULT_FOLDER_NAME,
    MAX_LENGTH_FOLDER_NAME,
    OUT_OF_BOUND_LENGTH,
    ;

    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 20
        private const val DEFAULT_NAME = "기본 폴더"

        fun of(
            folderName: String,
            originFolderNames: LinkedHashSet<String>,
        ): FolderNameStatus {
            val trimmedFolderName = folderName.trim()

            return when {
                trimmedFolderName.isBlank() -> EMPTY
                trimmedFolderName.length == MAX_LENGTH -> MAX_LENGTH_FOLDER_NAME
                trimmedFolderName.length !in MIN_LENGTH..MAX_LENGTH -> OUT_OF_BOUND_LENGTH
                trimmedFolderName == DEFAULT_NAME -> DEFAULT_FOLDER_NAME
                originFolderNames.contains(trimmedFolderName) -> DUPLICATE_NAME
                else -> OK
            }
        }
    }
}
