package osmandroid.iptv.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spencerstudios.com.jetdblib.JetDB;

public class RecvAdapter extends RecyclerView.Adapter<RecvAdapter.channelViewHolder> implements Filterable {

    private List<SimpleM3UParser.M3U_Entry> channelList,fullChannelList;

    private Context context;



    public RecvAdapter(Context context, List<SimpleM3UParser.M3U_Entry> channelList)
    {
     this.context = context;
     this.channelList = channelList;
     fullChannelList = new ArrayList<>(channelList);
    }

    @NonNull
    @Override
    public channelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_channel, parent, false);
        return new channelViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull channelViewHolder holder, final int position) {

        holder.textView.setText(channelList.get(position).getName());
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
                }else Toast.makeText(context,"NULL",Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    @Override
    public Filter getFilter() {
        return channelFilter;
    }

    private Filter channelFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<SimpleM3UParser.M3U_Entry> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length()==0)
            {
                filteredList.addAll(fullChannelList);
            }else
            {
                String pattern = constraint.toString().toLowerCase().trim();
                for(SimpleM3UParser.M3U_Entry item:fullChannelList)
                {
                    if(item.getName() !=null && item.getName().toLowerCase().contains(pattern))
                    {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            channelList.clear();
            if(results.values != null)
            {
                channelList.addAll((List)results.values);
                notifyDataSetChanged();
            }

        }
    };


    class channelViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imageView;
        TextView textView;


        channelViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.nametv);

        }
    }

    public void updateChannels(List<SimpleM3UParser.M3U_Entry> channelList)
    {
        this.channelList.clear();
        this.channelList.addAll(channelList);
        fullChannelList = new ArrayList<>(channelList);
        notifyDataSetChanged();

    }

    public void updateCategorySelection(String category)
    {
        this.channelList.clear();

        for(SimpleM3UParser.M3U_Entry item : fullChannelList)
        {
            if(category.equals("Others"))
            {
                if(item.getGroupTitle() == null || Objects.equals(item.getGroupTitle().trim(), ""))
                {
                    channelList.add(item);
                }
            }else if(category.equals("ALL"))
            {
                channelList.add(item);
            }else
                {
                    if(item.getGroupTitle() != null && item.getGroupTitle().equals(category))
                    {
                        channelList.add(item);
                    }
                }
            }



        notifyDataSetChanged();
    }

 }
