package com.on.turip.ui.folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.on.turip.databinding.ActivityFolderBinding
import com.on.turip.ui.common.base.BaseActivity

class FolderActivity : BaseActivity<ActivityFolderBinding>() {
    override val binding: ActivityFolderBinding by lazy {
        ActivityFolderBinding.inflate(layoutInflater)
    }

    private val folderAdapter: FolderAdapter by lazy {
        FolderAdapter(
            object : FolderViewHolder.FolderListener {
                override fun onRemoveClick(folderId: Long) {
                    // TODO: 폴더 삭제 바텀 시트 다이얼로그 보여주기
                    val bottomSheet: FolderRemoveBottomSheetFragment =
                        FolderRemoveBottomSheetFragment.instance()
                    bottomSheet.show(supportFragmentManager, "folder_remove")
                }

                override fun onItemClick(folderId: Long) {
                    // TODO: 폴더명 편집 바텀 시트 다이얼로그 보여주기
                    val bottomSheet: FolderModifyBottomSheetFragment =
                        FolderModifyBottomSheetFragment.instance()
                    bottomSheet.show(supportFragmentManager, "folder_modify")
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAdapters()
        setupListeners()
    }

    private fun setupAdapters() {
        binding.rvFolder.adapter = folderAdapter
    }

    private fun setupListeners() {
        binding.ivFolderFolderPlus.setOnClickListener {
            val bottomSheet: FolderAddBottomSheetFragment = FolderAddBottomSheetFragment.instance()
            bottomSheet.show(supportFragmentManager, "folder_add")
        }
        binding.ivFavoriteBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, FolderActivity::class.java)
    }
}
