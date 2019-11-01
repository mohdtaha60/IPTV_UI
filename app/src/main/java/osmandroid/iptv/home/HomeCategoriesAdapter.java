package osmandroid.iptv.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.categoriesViewHolder> {

    private List<List<SimpleM3UParser.M3U_Entry>> categoryList;
    List<String> namesList;
    private Context context;



    public HomeCategoriesAdapter(Context context,List<String> namesList, List<List<SimpleM3UParser.M3U_Entry>> categoryList)
    {
        this.context = context;
        this.categoryList = categoryList;
        this.namesList = namesList;
    }

    @NonNull
    @Override
    public categoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_single_item, parent, false);
        return new categoriesViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull categoriesViewHolder holder, final int position) {

        holder.textView.setText(namesList.get(position));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.hasFixedSize();

        HomeSingleCategoryAdapter adapter =  new HomeSingleCategoryAdapter(context,categoryList.get(position));

        holder.recyclerView.setAdapter(adapter);


    }

    @Override
    public int getItemCount() {
        return namesList.size();
    }




    class categoriesViewHolder extends RecyclerView.ViewHolder {


        TextView textView;
        RecyclerView recyclerView;


        categoriesViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.sRecyclerView);
            textView = itemView.findViewById(R.id.cname);

        }
    }

    public void updateCategories(List<String> namesList,List<List<SimpleM3UParser.M3U_Entry>> categoriesList)
    {
        this.categoryList.clear();
        this.namesList.clear();
        this.categoryList.addAll(categoriesList);
        this.namesList.addAll(namesList);

        notifyDataSetChanged();

    }



}
