package com.example.remembergroup.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.example.remembergroup.messenger2.R;
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
        //ViewHolder holder = null;

        @SuppressLint("ViewHolder") View row=inflater.inflate(resource,null);
        TextView txtName=row.findViewById(R.id.txtName);
        TextView txtText=row.findViewById(R.id.txtText);
        TextView txtTime=row.findViewById(R.id.txtTime);
        ImageView imgOnline=row.findViewById(R.id.imgOnline);

        //load id to controls
        ImageView imgImage=row.findViewById(R.id.imgImage);
        Button btnMenu=row.findViewById(R.id.btnMenu);

        Friend friend=filterData.get(position);

        //set values to each controls
        txtName.setText(friend.getName());
        txtText.setText(friend.getTextLast());

        SimpleDateFormat format=new SimpleDateFormat("MMM dd, yyyy");
        txtTime.setText(format.format(friend.getDateTime()));

        if(friend.getAvatar()!=null)
            imgImage.setImageBitmap(friend.getAvatar());
        if(friend.isOnline())
            imgOnline.setVisibility(View.VISIBLE);
        else
            imgOnline.setVisibility(View.INVISIBLE);

        //add events
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMenuClick(view);
            }
        });
        return row;
    }

    private void addEvents(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMenuClick(view);
            }
        });
    }

    private void handleMenuClick(View view) {
        PopupMenu popupMenu=new PopupMenu(this.context,view);
        MenuInflater inflater=popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.show();
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
    //View holder
    private static class ViewHolder
    {
        private TextView txtName;
        private TextView txtText;
        private TextView txtTime;
        private ImageView imgOnline;
        private ImageView imgImage;
        private Button btnMenu;

        public ViewHolder() {
        }

        public ViewHolder(View row)
        {
            txtName=row.findViewById(R.id.txtName);
            txtText=row.findViewById(R.id.txtText);
            txtTime=row.findViewById(R.id.txtTime);
            imgOnline=row.findViewById(R.id.imgOnline);
            imgImage=row.findViewById(R.id.imgImage);
            btnMenu=row.findViewById(R.id.btnMenu);
        }
    }
}
