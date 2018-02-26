package com.mahindra.be_lms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.constant.FileType;
import com.mahindra.be_lms.util.Constants;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ImportActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "ImportActivity";

    private static final String PREF_NAME = "LocPref";
    private static final String PREF_LAST_LOC = "last_location";

    // Members
    private File curDir = new File(Environment.getExternalStorageDirectory().getPath());
    private File[] files;
    // Views
    private ListView lvFiles;
    private List<String> fileExtentions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_activity);
        lvFiles = (ListView) findViewById(R.id.lvFiles);
        lvFiles.setFocusable(false);
        lvFiles.setClickable(false);
        lvFiles.setOnItemClickListener(this);

        //get file type
        int fileType = getIntent().getIntExtra("file_type", 0);
        switch (fileType) {
            case FileType.VIDEO:
                fileExtentions.add(".mp4");
                fileExtentions.add(".mpeg");

                break;
            case FileType.HTML:
                fileExtentions.add(".html");
                break;
            case FileType.PDF:
                fileExtentions.add(".pdf");
                break;
            case FileType.EXCEL:
                fileExtentions.add(".xls");
                fileExtentions.add(".xlsx");
                break;
            case FileType.WORD:
                fileExtentions.add(".docx");
                break;

        }

        // restore last import location
        SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String lastLocation = pref.getString(PREF_LAST_LOC, Environment
                .getExternalStorageDirectory().getPath());
        Log.d(TAG, "lastLocation: " + lastLocation);
        curDir = new File(lastLocation);

        refreshList();
    }

    /**
     * Refreshes current list
     */
    private void refreshList() {
        Log.i(TAG, "[METHOD] void refreshList()");

        // List files.
        files = curDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory() && !file.getName().startsWith(".")) {
                    return true;
                } else if (file.isFile()) {
                    for (String fileExt : fileExtentions) {
                        if (file.getName().toLowerCase().endsWith(fileExt)) {
                            return true;
                        }
                    }
                }
                return false;
            }

        });

        if (files == null) {
            // create an empty file list
            files = new File[0];
        }

        // Sort files.
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                boolean isDir1 = f1.isDirectory();
                boolean isDir2 = f2.isDirectory();
                if (isDir1 && !isDir2)
                    return -1;
                if (!isDir1 && isDir2)
                    return 1;
                return f1.getName().compareToIgnoreCase(f2.getName());
            }
        });

        // Add up directory?
        if (curDir.getParent() != null) {
            File[] temp = new File[files.length + 1];
            System.arraycopy(files, 0, temp, 1, files.length);
            temp[0] = new File("..");
            files = temp;
        }

        // Build names.
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();
        }

        // set single choice mode
        lvFiles.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, names));
        lvFiles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // save this location into shared preference
        SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        String curPath = curDir.getPath().toString();
        Log.d(TAG, "curPath: " + curPath);
        editor.putString(PREF_LAST_LOC, curPath);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "[CALLBACK] boolean onCreateOptionsMenu(menu:" + menu + ")");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "[CALLBACK] boolean onOptionsItemSelected(item:" + item + ")");

        switch (item.getItemId()) {
            case R.id.menu_import:
                long[] checked = lvFiles.getCheckItemIds();
                ArrayList<String> checkedList = new ArrayList<String>();

                for (long id : checked) {
                    File file = files[(int) id];
                    String epubPath = file.getPath();
                    Log.d(TAG, "file path: " + epubPath);

                    checkedList.add(epubPath);
                }
                Log.d(TAG, "checkedList: " + checkedList);

                Intent intent = getIntent();
                intent.putStringArrayListExtra(Constants.PATH_LIST, checkedList);
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.menu_cancel:
                finish();
                break;
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "[CALLBACK] void onListItemClick(ListView l, View v, int position, long id)");

        File file = files[position];
        if (file.getPath().equals("..")) {
            curDir = curDir.getParentFile();
            refreshList();
        } else if (file.isDirectory()) {
            curDir = file;
            refreshList();
        }
    }
}
