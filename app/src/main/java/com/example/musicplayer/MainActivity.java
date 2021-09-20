package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listSong);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Permission Given!", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = getSongs(Environment.getExternalStorageDirectory());
                        String [] arrayItems = new String[mySongs.size()];
                        for(int i=0; i<mySongs.size(); i++) {
                            arrayItems[i] = mySongs.get(i).getName().replace(".mp3","");
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayItems);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, SongPlayer.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("listSongs",mySongs);
                                intent.putExtra("currentSong",currentSong);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                    }


                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
    }

    public ArrayList<File> getSongs(File file) {
        ArrayList arrayList = new ArrayList();
        File [] Songs = file.listFiles();
        if(Songs != null) {
            for(File myFile: Songs) {
                if(!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(getSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mps") && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}