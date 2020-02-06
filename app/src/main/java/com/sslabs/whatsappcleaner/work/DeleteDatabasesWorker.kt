package com.sslabs.whatsappcleaner.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteDatabasesWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val DELETE_WORK_NAME = "cleanup-databases-work"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Result.success()
    }
}
