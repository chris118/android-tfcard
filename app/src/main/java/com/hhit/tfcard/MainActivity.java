package com.hhit.tfcard;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int STORAGE_REQUEST_CODE = 100;
    private static final String STORAGE_DEFAULT_URI = "STORAGE_DEFAULT_URI";


    private String rootPath = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        File[] externals = this.getExternalFilesDirs("external");
//        rootPath = externals[0].getPath();
//        if (DocumentsUtils.checkWritableRootPath(this, rootPath)) {
//            showOpenDocumentTree();
//        }

        requestPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestPermission() {
//        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
//        List<StorageVolume> volume = sm.getStorageVolumes();
//        int size = volume.size();
//        Log.i(TAG, "size: " + size);
//        for (int i = 1; i < size; i++) {
//            StorageVolume storageVolume = volume.get(i);
//            Intent intent = storageVolume.createAccessIntent(null);
//            startActivityForResult(intent, REQUEST_REQUEST_CODE);
//        }

        String uri = PreferenceManager.getDefaultSharedPreferences(this).getString(STORAGE_DEFAULT_URI, "");
        if(uri.length() > 0) { // 已经授权
            Uri treeUri = Uri.parse(uri);
            listFiles(treeUri);
        }else { // 未授权
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, STORAGE_REQUEST_CODE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == STORAGE_REQUEST_CODE) {
            Uri treeUri = resultData.getData();


            // takePersistableUriPermission()。系统将保留此 URI，后续的访问请求将返回 RESULT_OK，且不会向用户显示确认 UI
            final int takeFlags = resultData.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

            // 将获得的uri保存起来，方便再次使用
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(STORAGE_DEFAULT_URI, treeUri.toString()).commit();

            listFiles(treeUri);
        }
    }

    private void listFiles(Uri treeUri) {
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

        // List all existing files inside picked directory
        for (DocumentFile file : pickedDir.listFiles()) {
            Log.d(TAG, "Found file " + file.getName() + " with size " + file.length());
        }

        // Create a new file and write into it
        DocumentFile newFile = pickedDir.createFile("text/plain", "My Novel");
        OutputStream out = null;
        try {
            out = getContentResolver().openOutputStream(newFile.getUri());
            out.write("A long time ago...".getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }









//    private void showOpenDocumentTree() {
//        Intent intent = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            StorageManager sm = this.getSystemService(StorageManager.class);
//
//            StorageVolume volume = sm.getStorageVolume(new File(rootPath));
//
//            if (volume != null) {
//                intent = volume.createAccessIntent(null);
//            }
//        }
//
//        if (intent == null) {
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//        }
//        startActivityForResult(intent, DocumentsUtils.OPEN_DOCUMENT_TREE_CODE);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case DocumentsUtils.OPEN_DOCUMENT_TREE_CODE:
//                if (data != null && data.getData() != null) {
//                    Uri uri = data.getData();
//                    DocumentsUtils.saveTreeUri(this, rootPath, uri);
//                }
//                break;
//            default:
//                break;
//        }
//    }
}











