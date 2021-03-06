package com.example.remembergroup.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.remembergroup.chat_app.ChatActivity;
import com.example.remembergroup.chat_app.MemoryManager;
import com.example.remembergroup.chat_app.ProfileFriendActivity;
import com.example.remembergroup.chat_app.ProfileMeActivity;
import com.example.remembergroup.chat_app.R;
import com.example.remembergroup.model.Conversation;
import com.example.remembergroup.model.Friend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chauvansang on 9/21/2017.
 */

public class FriendAdapter extends ArrayAdapter<Friend>  {
    @NonNull
    private Activity context;
    @LayoutRes
    private int resource;
    @NonNull
    private List<Friend> objects;

    private List<Friend> filterData;
    public FriendAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<Friend> objects) {
        super(context, resource, objects);
        this.filterData=objects;
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get layout inflater
        LayoutInflater inflater=context.getLayoutInflater();

        @SuppressLint("ViewHolder") View row=inflater.inflate(resource,null);
        TextView txtName=row.findViewById(R.id.txtName);
        ImageView imgOnline=row.findViewById(R.id.imgOnline);
        ImageView imgImage=row.findViewById(R.id.imgImage);
        Button btnProfile = row.findViewById(R.id.btnProfile);

        final Friend friend=filterData.get(position);

        //set values to each controls
        txtName.setText(friend.getName());

        if(MemoryManager.getInstance().getBitmapFromMemCache(friend.getEmail())!=null)
            imgImage.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(friend.getEmail()));
        if(friend.isOnline())
            imgOnline.setVisibility(View.VISIBLE);
        else
            imgOnline.setVisibility(View.INVISIBLE);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProfileFriendActivity.class);
                i.putExtra("PROFILEFRIEND", friend);
                context.startActivity(i);
            }
        });

        return row;
    }


    @Override
    public int getCount() {
        return filterData.size();
    }


    @Nullable
    @Override
    public Friend getItem(int position) {
        return filterData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();


                ArrayList<Friend> friends=new ArrayList<>();
                Friend temp;
                final FilterResults result = new FilterResults();
                for(int i=0;i<objects.size();i++)
                {
                    temp=objects.get(i);
                    if(temp.getName().toLowerCase().contains(filterString))
                    {
                        friends.add(objects.get(i));
                    }
                }

                result.count=friends.size();
                result.values=friends;

                return result;
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filterData= (List<Friend>) filterResults.values;
                    notifyDataSetChanged();
            }
        };
    }
}
