package osmandroid.iptv.home;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import osmandroid.iptv.home.ui.livetv.LiveTvFragment;
import spencerstudios.com.jetdblib.JetDB;

public class Main3Activity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private AppBarConfiguration mAppBarConfiguration;
    NavController navController;

    //SearchView searchView;
    //MenuItem searchItem;
    //ArrayAdapter spinnerAdapter;
    //AppCompatSpinner spinner;



    private static final int SPEECH_REQUEST_CODE = 10;
    private static final int PICK_IMAGE_CONST = 101;


    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;
    private ArrayList<SimpleM3UParser.M3U_Entry> channelList = new ArrayList<>();
    ChannelLiveModel channelLiveModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_livetv, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(navController.getCurrentDestination().getId() != R.id.nav_livetv)
                   navController.navigate(R.id.nav_livetv);
            }
        });


        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("iptvt1.m3u");


        channelList = new ArrayList<>();
        //channelList = getChannelsList();

        channelLiveModel = ViewModelProviders.of(this).get(ChannelLiveModel.class);



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("channels");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                channelList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post

                    ChannelModel channelModel = postSnapshot.getValue(ChannelModel.class);
                    SimpleM3UParser.M3U_Entry m3U_entry = new SimpleM3UParser.M3U_Entry();
                    if(channelModel!=null)
                    {
                        m3U_entry.setName(channelModel.getName());
                        m3U_entry.setGroupTitle(channelModel.getGroup_title());
                        m3U_entry.setTvgLogo(channelModel.getLogo_url());
                        m3U_entry.setUrl(channelModel.getVideo_url());

                        channelList.add(m3U_entry);
                    }

                }

                Toast.makeText(Main3Activity.this,"Size: "+channelList.size(),Toast.LENGTH_LONG).show();
                ViewModelProviders.of(Main3Activity.this).get(ChannelLiveModel.class).setChannels(channelList);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });


    }


    ArrayList<SimpleM3UParser.M3U_Entry> playlist = null;

    ArrayList<SimpleM3UParser.M3U_Entry> getChannelsList()
    {


        try {
            final File localFile = new File(getFilesDir(), "all.m3u");

            //localFile = File.createTempFile("audio", "x-mpegurl");

            mStorageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                            try {
                                Log.d(TAG, "Entered Parser");
                                playlist = new SimpleM3UParser().parse(new FileInputStream(localFile));
                                Log.d(TAG, "Parsing Completed: length " + playlist.size());
                                JetDB.putListOfObjects(Main3Activity.this, playlist, "channelList");
                                ViewModelProviders.of(Main3Activity.this).get(ChannelLiveModel.class).setChannels(playlist);

                                /*
                                for (SimpleM3UParser.M3U_Entry item : playlist) {
                                    Log.d(TAG, "Name : " + item.getName());
                                    Log.d(TAG, "Group Title : " + item.getGroupTitle());
                                    Log.d(TAG, "TvgEpg : " + item.getTvgEpgUrl());
                                    Log.d(TAG, "TvgId : " + item.getTvgId());
                                    Log.d(TAG, "TvgLogo : " + item.getTvgLogo());
                                    Log.d(TAG, "URL : " + item.getUrl());
                                }
*/
                            } catch (IOException e) {
                                Log.d(TAG, "Error Parsing");
                                e.printStackTrace();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                    //TODO show retry dialog -- make sure to optimize code to call it in retry
                    Toast.makeText(Main3Activity.this, "Error Loading file: "+exception.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error Error");
                }
            });

            return playlist;
        }catch (Exception e)
        {
            Log.d(TAG, "Error File");

        }

        return playlist;
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
                        FragmentManager fm = getSupportFragmentManager();
                        LiveTvFragment fragment =
                                (LiveTvFragment) fm.findFragmentById(R.id.nav_livetv);
                        if (fragment != null) {
                            fragment.submitVoiceText(arrayList.get(0));
                        }


                    }

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



}
