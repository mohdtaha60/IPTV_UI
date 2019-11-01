package osmandroid.iptv.home.ui.livetv;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveTvViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LiveTvViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is live tv fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }


}