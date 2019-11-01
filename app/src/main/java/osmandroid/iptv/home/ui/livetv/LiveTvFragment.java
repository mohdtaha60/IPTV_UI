package osmandroid.iptv.home.ui.livetv;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import osmandroid.iptv.home.ChannelLiveModel;
import osmandroid.iptv.home.R;
import osmandroid.iptv.home.RecvAdapter;
import osmandroid.iptv.home.SimpleM3UParser;

public class LiveTvFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private SearchView searchView;
    private MenuItem searchItem;
    private ArrayAdapter spinnerAdapter;
    private AppCompatSpinner spinner;


    private Context context;

    private static final String TAG = "TAG";
    private RecyclerView recyclerView;
    private RecvAdapter adapter;




    private List<SimpleM3UParser.M3U_Entry> channelList;

    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;
    private ArrayList<String> categoryList = new ArrayList<>();

    private ChannelLiveModel channelLiveModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_livetv, container, false);

        context = root.getContext();





        recyclerView = root.findViewById(R.id.recyclerView);

        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("iptvt1.m3u");


        channelList = new ArrayList<>();



        GridLayoutManager mGridLayoutManager = new GridLayoutManager(Objects.requireNonNull(getActivity()).getBaseContext(), 3);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.hasFixedSize();

        adapter =  new RecvAdapter(getActivity(),channelList);

        recyclerView.setAdapter(adapter);


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        channelLiveModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ChannelLiveModel.class);

        channelLiveModel.getChannels().observe(this, new Observer<ArrayList<SimpleM3UParser.M3U_Entry>>() {
            @Override
            public void onChanged(ArrayList<SimpleM3UParser.M3U_Entry> m3U_entries) {
                //Toast.makeText(getActivity(),"Data changed:"+m3U_entries.size(),Toast.LENGTH_SHORT).show();
                channelList = new ArrayList<>(m3U_entries);
                adapter.updateChannels(channelList);
                if(channelList.size()>0)
                updateCategories(channelList);

            }


        });



    }

    void getFilePerformStuff()
    {
        try {
            final File localFile = new File(context.getFilesDir(), "all.m3u");

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
                    Toast.makeText(context, "Error Loading file", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error Error");
                }
            });

        }catch (Exception e)
        {
            Log.d(TAG, "Error File");
        }


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


        //TODO call this 2 lines in activity
        if(spinnerAdapter!=null)
        {
            spinnerAdapter.notifyDataSetChanged();
            spinner.setSelection(categoryList.size()-1);
        }



    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.main_menu,menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();



        MenuItem spinnerItem = menu.findItem(R.id.action_category);
        spinner = (AppCompatSpinner) spinnerItem.getActionView();


        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                //todo hide keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });


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



    public void submitVoiceText(String query)
    {
        Log.d(TAG, "submitVoiceText: ");
        searchItem.expandActionView();
        searchView.setQuery(query,true);

    }
}