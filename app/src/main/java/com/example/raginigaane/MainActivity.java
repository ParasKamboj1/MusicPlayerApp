package com.example.raginigaane;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;
    ListView listView;
    String[] items;
    public void check(){
        if (checkPermission()) {
            Toast.makeText(this, "Fetching Songs Please Wait... maximum 20 seconds", Toast.LENGTH_LONG).show();
            new FindSongsTask().execute();
        } else {
            requestPermission();
        }}
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

        check();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fetching Songs Please Wait... maximum 20 seconds", Toast.LENGTH_LONG).show();
                new FindSongsTask().execute();
            } else {
                // Handle permission denied
                Toast.makeText(this, "Please Allow the permission for songs..", Toast.LENGTH_SHORT).show();
                check();
            }
        }
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
