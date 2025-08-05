package com.on.turip.data.initializer

import com.google.firebase.installations.FirebaseInstallations
import com.on.turip.domain.settingsStorage.repository.UserStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseInstallationsInitializer(
    private val repository: UserStorageRepository,
) {
    fun setupFirebaseInstallationId(coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                coroutineScope.launch {
                    repository.createId(task.result)
                }
            } else {
                // TODO : Firebase Installation ID 가져오지 못했을 때
            }
        }
    }
}
