package com.example.remembergroup.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remembergroup.chat_app.R;
import com.example.remembergroup.chat_app.SingletonSocket;
import com.example.remembergroup.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khai Lee on 12/28/2017.
 */

public class UserAdapter extends ArrayAdapter<User> {
    private final String CLIENT_REQUEST_ADD_FRIEND = "CLIENT_REQUEST_ADD_FRIEND";

    @NonNull
    private Activity context;
    @LayoutRes
    private int resource;
    @NonNull
    private List<User> objects;

    private List<User> filterData;


    public UserAdapter(@NonNull Activity context, int resource, @NonNull List<User> objects) {
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

        @SuppressLint("ViewHolder")
        View row=inflater.inflate(resource,null);
        final TextView txtName = row.findViewById(R.id.txtName);
        ImageView imgAvatar = row.findViewById(R.id.imgAvatar);
        final Button btnAddF = row.findViewById(R.id.btnAddF);

        final User user = filterData.get(position);

        //set values to each controls
        txtName.setText(user.getName());

        if(user.getAvatar()!=null)
            imgAvatar.setImageBitmap(user.getAvatar());

        //add events
        btnAddF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingletonSocket.getInstance().mSocket.emit(CLIENT_REQUEST_ADD_FRIEND, user.getEmail());

                objects.remove(user);
                notifyDataSetChanged();
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
    public User getItem(int position) {
        return filterData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();

                ArrayList<User> users=new ArrayList<>();
                User temp;
                final FilterResults result = new FilterResults();

                for(int i=0;i<objects.size();i++)
                {
                    temp=objects.get(i);
                    if(temp.getName().toLowerCase().contains(filterString))
                    {
                        users.add(objects.get(i));
                    }
                }

                result.count = users.size();
                result.values = users;

                return result;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterData = (List<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}