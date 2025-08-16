package com.on.turip.ui.folder

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
                }

                override fun onItemClick(folderId: Long) {
                    // TODO: 폴더명 편집 바텀 시트 다이얼로그 보여주기
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rvFolder.adapter = folderAdapter
    }
}
