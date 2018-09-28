package com.sslabs.whatsappcleaner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASES_PATH = "WhatsApp/Databases";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ensurePermissions();
    }

    public void onDeleteDatabasesClick(View view) {
        deleteBackupDatabases();
    }

    private void deleteBackupDatabases() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File databasesPath = new File(Environment.getExternalStorageDirectory(), DATABASES_PATH);
            File[] backupDatabases = databasesPath.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return Pattern.matches("^msgstore-.*\\.db\\.crypt12$", pathname.getName());
                }
            });
//            if (dbFile.exists()) {
//                dbFile.delete();
//            }
        }
    }

    private void ensurePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //TODO: how should it behave?
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
        }
    }
}
