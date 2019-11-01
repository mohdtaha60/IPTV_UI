package osmandroid.iptv.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewSliderAdapter extends PagerAdapter {
    private Context context;
    public String imageUrl;
    ViewPager viewPager;


    private List<CommonModels> listSlider=new ArrayList<>();


    private List<CommonModels> list = new ArrayList<>();

    public ViewSliderAdapter(Context context, List<CommonModels> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_slider,null);
        View lyt_parent = view.findViewById(R.id.lyt_parent);
        final CommonModels models=list.get(position);
        TextView textView = view.findViewById(R.id.textView);

        textView.setText(models.getTitle());

        ImageView imageView=view.findViewById(R.id.imageview);
        Glide.with(context).load(models.getImage_url()).into(imageView);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
    }


