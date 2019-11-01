package osmandroid.iptv.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import spencerstudios.com.jetdblib.JetDB;


public class HomeSingleCategoryAdapter extends RecyclerView.Adapter<HomeSingleCategoryAdapter.categoriesViewHolder> {

    private List<SimpleM3UParser.M3U_Entry> channelList;
    private Context context;



    public HomeSingleCategoryAdapter(Context context, List<SimpleM3UParser.M3U_Entry> channelList)
    {
        this.context = context;
        this.channelList = channelList;
    }

    @NonNull
    @Override
    public categoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_live_tv_home, parent, false);
        return new categoriesViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull categoriesViewHolder holder, final int position) {

        holder.textView.setText(channelList.get(position).getName());

        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.imageView.setBackgroundColor(currentColor);

        Glide.with(context).load(channelList.get(position).getTvgLogo()).centerCrop().into(holder.imageView);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(channelList.get(position).getUrl() !=null && !channelList.get(position).getUrl().trim().equals(""))
                {
                    try {
                        JetDB.putListOfObjects(context, channelList, "channelList");
                    }catch (Exception ignored){
                    }

                    Intent intent = new Intent(context,PlayerActivity.class);
                    intent.putExtra("video_path",channelList.get(position).getUrl());
                    intent.putExtra("pos",position);
                    context.startActivity(intent);
                }else Toast.makeText(context,"Not Available",Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }




    class categoriesViewHolder extends RecyclerView.ViewHolder {


        TextView textView;
        ImageView imageView;
        CardView cardView;



        categoriesViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.lyt_parent);

        }
    }

    public void updateCategories(List<SimpleM3UParser.M3U_Entry> channelList)
    {
        this.channelList.clear();
        this.channelList.addAll(this.channelList);
        notifyDataSetChanged();

    }



}


