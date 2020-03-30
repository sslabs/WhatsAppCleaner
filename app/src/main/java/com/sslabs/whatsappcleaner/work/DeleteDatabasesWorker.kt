package com.sslabs.whatsappcleaner.work

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DeleteDatabasesWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val DELETE_WORK_NAME = "cleanup-databases-work"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        filesToDelete()
        Result.success()
    }

    private fun filesToDelete() {
        val oi = applicationContext.getExternalFilesDir(null)
        val databasesPath = File(Environment.getExternalStorageDirectory(), "WhatsApp/Databases")
    }

//    private File[] filesToDelete() {
//        File databasesPath =
//        new File(Environment.getExternalStorageDirectory(), "WhatsApp/Databases");
//        File[] oldBackupDatabases = databasesPath.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                return Pattern.matches("^msgstore-.*\\.db\\.crypt12$",
//                    pathname.getName());
//            }
//        });
//        return oldBackupDatabases;
//    }
}
