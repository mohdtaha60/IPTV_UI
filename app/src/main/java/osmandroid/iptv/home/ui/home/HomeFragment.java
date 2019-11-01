package osmandroid.iptv.home.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.viewpager.widget.ViewPager;
import osmandroid.iptv.home.ChannelLiveModel;
import osmandroid.iptv.home.CommonModels;
import osmandroid.iptv.home.HomeCategoriesAdapter;
import osmandroid.iptv.home.R;
import osmandroid.iptv.home.RecvAdapter;
import osmandroid.iptv.home.SimpleM3UParser;
import osmandroid.iptv.home.ViewSliderAdapter;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ViewPager viewPager;
    CirclePageIndicator indicator;

    private List<CommonModels> listSlider=new ArrayList<>();

    List<SimpleM3UParser.M3U_Entry> channelList;
    ChannelLiveModel channelLiveModel;

    RecyclerView recyclerView;
    HomeCategoriesAdapter adapter;
    private ViewSliderAdapter sliderAdapter;
    private View sliderLayout;
    private Timer timer;

    public View onCreateView(@NonNull  LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        sliderLayout = root.findViewById(R.id.slider_layout);
        viewPager = root.findViewById(R.id.viewPager);
        indicator=root.findViewById(R.id.indicator);

        recyclerView = root.findViewById(R.id.homeRecyclerView);
        channelList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();

        listSlider.add(new CommonModels("Test1,", "https://i.ytimg.com/vi/AwWaYdROdGs/maxresdefault.jpg","https://feeds.intoday.in/hltapps/api/master.m3u8"));
        listSlider.add(new CommonModels("Test2,", "https://resize.indiatvnews.com/en/resize/newbucket/715_-/2019/04/ipl-live-1554366568.jpg","https://timesnow.airtel.tv/live/MN_pull/master.m3u8"));
        listSlider.add(new CommonModels("Test3,", "https://wikibio.in/wp-content/uploads/2019/07/Hindustani-Bhau.jpg","rtmp://103.250.39.13:1935/dw3/4tvnews.flv"));

        sliderAdapter = new ViewSliderAdapter(getActivity(),listSlider);
        viewPager.setAdapter(sliderAdapter);
        indicator.setViewPager(viewPager);

        //----init timer slider--------------------
        timer = new Timer();


        List<String> nameList = getCategoryList(channelList);
        //nameList.remove(nameList.size()-1);
        //nameList.remove(nameList.size()-1);
        adapter =  new HomeCategoriesAdapter(getActivity(),nameList,getmultiHomeLists());

        recyclerView.setAdapter(adapter);

        return root;

    }

    //----timer for auto slide------------------
    public class SliderTimer extends TimerTask {
        Activity activity;
        public SliderTimer(Activity activity) {
            this.activity= activity;
        }

        @Override
        public void run() {

            if (activity!=null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < listSlider.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer=new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(getActivity()), 5000, 5000);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        channelLiveModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ChannelLiveModel.class);

        channelLiveModel.getChannels().observe(this, new Observer<ArrayList<SimpleM3UParser.M3U_Entry>>() {
            @Override
            public void onChanged(ArrayList<SimpleM3UParser.M3U_Entry> m3U_entries) {
                channelList = new ArrayList<>(m3U_entries);
                List<String> nameList = getCategoryList(channelList);
                //nameList.remove(nameList.size()-1);
                //nameList.remove(nameList.size()-1);
                adapter.updateCategories(nameList,getmultiHomeLists());

            }
        });

    }


    List<List<SimpleM3UParser.M3U_Entry>> getmultiHomeLists()
    {
        List<List<SimpleM3UParser.M3U_Entry>> multiHomeList = new ArrayList<>();
        List<String> categoryList = getCategoryList(channelList);
        //categoryList.remove(categoryList.size()-1);

        for(int i=0;i<categoryList.size();i++)
        {
            multiHomeList.add(new ArrayList<SimpleM3UParser.M3U_Entry>());
        }

        for(int i=0;i<channelList.size();i++) {

           for(int j=0;j<categoryList.size();j++)
           {
               if(channelList.get(i).getGroupTitle() != null && !Objects.equals(channelList.get(i).getGroupTitle().trim(), ""))
               {
                   if(channelList.get(i).getGroupTitle().equals(categoryList.get(j)))
                   {
                       (multiHomeList.get(j)).add(channelList.get(i));
                   }
               }else
               {
                   (multiHomeList.get(categoryList.size()-1)).add(channelList.get(i));
               }


           }
        }

        return multiHomeList;
    }


    List<String> getCategoryList(List<SimpleM3UParser.M3U_Entry> channelList) {

        List<String> categoryList = new ArrayList<>();

        boolean isnulladded = false;

        categoryList.clear();
        for(SimpleM3UParser.M3U_Entry item: channelList)
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



        //categoryList.add("ALL");

        return categoryList;

    }


}