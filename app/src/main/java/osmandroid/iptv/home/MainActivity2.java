package osmandroid.iptv.home;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TAG";
    private static final int SPEECH_REQUEST_CODE = 10;
    private static final int PICK_IMAGE_CONST = 101;
    RecyclerView recyclerView;
    RecvAdapter adapter;

    SearchView searchView;
    MenuItem searchItem;
    ArrayAdapter spinnerAdapter;
    AppCompatSpinner spinner;


    List<SimpleM3UParser.M3U_Entry> channelList;

    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;
    private ArrayList<String> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recyclerView);

        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("iptvt1.m3u");


        channelList = new ArrayList<>();



        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.hasFixedSize();

        adapter =  new RecvAdapter(this,channelList);

        recyclerView.setAdapter(adapter);

        getFilePerformStuff();

    }








    void getFilePerformStuff()
    {
        try {
            final File localFile = new File(getFilesDir(), "all.m3u");

            //localFile = File.createTempFile("audio", "x-mpegurl");

            mStorageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            List<SimpleM3UParser.M3U_Entry> playlist = null;

                            try {
                                Log.d(TAG, "Entered Parser");
                                playlist = new SimpleM3UParser().parse(new FileInputStream(localFile));
                                //playlist = new SM3UParser().parseFile(new FileInputStream(localFile));
                                Log.d(TAG, "Parsing Completed: length " + playlist.size());
                                updateCategories(playlist);


                                for (SimpleM3UParser.M3U_Entry item : playlist) {
                                    Log.d(TAG, "Name : " + item.getName());
                                    Log.d(TAG, "Group Title : " + item.getGroupTitle());
                                    Log.d(TAG, "TvgEpg : " + item.getTvgEpgUrl());
                                    Log.d(TAG, "TvgId : " + item.getTvgId());
                                    Log.d(TAG, "TvgLogo : " + item.getTvgLogo());
                                    Log.d(TAG, "URL : " + item.getUrl());
                                }



                            } catch (IOException e) {
                                Log.d(TAG, "Error Parsing");
                                e.printStackTrace();
                            }

                            adapter.updateChannels(playlist);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                    //TODO show retry dialog -- make sure to optimize code to call it in retry
                    Toast.makeText(MainActivity2.this, "Error Loading file", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error Error");
                }
            });

        }catch (Exception e)
        {
            Log.d(TAG, "Error File");
        }


    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();



        MenuItem spinnerItem = menu.findItem(R.id.action_category);
        spinner = (AppCompatSpinner) spinnerItem.getActionView();

        spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.signoutM:
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_mic:
                getSpeechInput();
                return true;


        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        //Toast.makeText(getApplicationContext(),categoryList.get(position) , Toast.LENGTH_LONG).show();
        adapter.updateCategorySelection(categoryList.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case SPEECH_REQUEST_CODE:
                if(resultCode == RESULT_OK && data!=null)
                {
                    ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (arrayList != null) {
                        //searchView.onActionViewExpanded();
                        searchItem.expandActionView();
                        searchView.setQuery(arrayList.get(0),true);
                    }

                }
                break;

            case PICK_IMAGE_CONST:




                Uri uri = null;
                try {

                    // When an Image is picked
                    if (requestCode == PICK_IMAGE_CONST && resultCode == RESULT_OK
                            && null != data) {
                        // Get the Image from data


                        uri=data.getData();
                        //updateCursor(uri);

                        String absolutePath2=null;
                        //setting the image via the uri

                        Log.d(TAG, "absolute path : "+uri.getPath());
                        absolutePath2 = getPath(uri);




                        try {


                            List<SimpleM3UParser.M3U_Entry> playlist = new SimpleM3UParser().parse(absolutePath2);

                            Log.d(TAG, "onCreate: size"+playlist.size());
                            Log.d(TAG, "onActivityResult: "+playlist.toString());
                            Log.d(TAG, "onCreate: "+playlist.get(0).getName());

                            updateCategories(playlist);
                            adapter.updateChannels(playlist);

                            for(SimpleM3UParser.M3U_Entry item: playlist)
                            {
                                Log.d(TAG, "Name : "+item.getName());
                                Log.d(TAG, "Group Title : "+item.getGroupTitle());
                                Log.d(TAG, "TvgEpg : "+item.getTvgEpgUrl());
                                Log.d(TAG, "TvgId : "+item.getTvgId());
                                Log.d(TAG, "TvgLogo : "+item.getTvgLogo());
                                Log.d(TAG, "URL : "+item.getUrl());
                            }



                        }catch (Exception e)
                        {
                            Log.d(TAG, "playlist result: "+ e.toString());
                        }



                    }else
                    {
                        Toast.makeText(this, "You haven't picked File",
                                Toast.LENGTH_LONG).show();
                    }





                } catch (Exception e) {

                    Toast.makeText(this, "Something went wrong: "+e.toString(), Toast.LENGTH_LONG)
                            .show();
                }



                break;
        }
    }

    public void getSpeechInput()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        startActivityForResult(intent,SPEECH_REQUEST_CODE);
    }

    private void updateCategories(List<SimpleM3UParser.M3U_Entry> playlist) {

        boolean isnulladded = false;

        categoryList.clear();
        for(SimpleM3UParser.M3U_Entry item: playlist)
        {
            if(item.getGroupTitle() == null || Objects.equals(item.getGroupTitle().trim(), ""))
            {
                if(!isnulladded)
                {
                    categoryList.add("Others");
                    isnulladded=true;
                }
            }else {
                if(!categoryList.contains(item.getGroupTitle()))
                    categoryList.add(item.getGroupTitle());
            }

        }



        categoryList.add("ALL");

        spinnerAdapter.notifyDataSetChanged();
        spinner.setSelection(categoryList.size()-1);

    }










    void getFilePerformStuff2()
    {
        final long ONE_MB = 1024 * 1024;
        mStorageRef.getBytes(ONE_MB)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        try {

                            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                            ObjectInputStream ois = new ObjectInputStream(bis);
                            File fileFromBytes = (File) ois.readObject();


                            List<SimpleM3UParser.M3U_Entry> playlist = new SimpleM3UParser().parse(bis);
                            adapter.updateChannels(playlist);


                            bis.close();
                            ois.close();

                        }catch (Exception e)
                        {
                            Toast.makeText(MainActivity2.this,"Error Occured:"+e.toString(),Toast.LENGTH_SHORT).show();
                        }





                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


}


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null)
            startActivity(new Intent(this,LoginActivity.class));
    }



    void getFilePerformStuff3()
    {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select File"), PICK_IMAGE_CONST);
    }





    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }



}
