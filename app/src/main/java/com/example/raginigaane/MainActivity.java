package com.example.raginigaane;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1E6CD6"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(R.string.app_name);
        int titleColor = getResources().getColor(R.color.white);
        int t = 2;

        listView = findViewById(R.id.listview);

        runTimePermission();
    }

    public void runTimePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                new FindSongsTask().execute();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                System.out.println("hello gurjot");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public class FindSongsTask extends AsyncTask<Void, Void, ArrayList<File>> {
        @Override
        protected ArrayList<File> doInBackground(Void... voids) {
            return findSong(Environment.getExternalStorageDirectory());
        }

        @Override
        protected void onPostExecute(ArrayList<File> result) {
            // Update UI with the result
            handleSongs(result);
        }
    }

    private void handleSongs(ArrayList<File> mySongs) {
        items = new String[mySongs.size()];
        for (int k = 0; k < mySongs.size(); k++) {
            items[k] = mySongs.get(k).getName().replace(".mp3", "").replace(".wav", "");
        }
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                        .putExtra("pos", i));
            }
        });
    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findSong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                        arrayList.add(singleFile);
                    }
                }
            }
        }
        return arrayList;
    }

    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView txtSong = view1.findViewById(R.id.textSong);
            txtSong.setSelected(true);
            txtSong.setText(items[position]);
            return view1;
        }
    }
}
