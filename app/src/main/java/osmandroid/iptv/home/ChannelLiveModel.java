package osmandroid.iptv.home;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ChannelLiveModel extends ViewModel {

    private MutableLiveData<ArrayList<SimpleM3UParser.M3U_Entry>> channelsList;

    public ChannelLiveModel() {
        channelsList = new MutableLiveData<>();
    }


    public LiveData<ArrayList<SimpleM3UParser.M3U_Entry>> getChannels() {
        return channelsList;
    }

    public void setChannels(ArrayList<SimpleM3UParser.M3U_Entry> list)
    {
        Log.d(TAG, "setChannels: new data");
        channelsList.setValue(list);
    }





}