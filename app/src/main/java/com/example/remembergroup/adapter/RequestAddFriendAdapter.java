package com.example.remembergroup.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
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

import com.example.remembergroup.chat_app.MemoryManager;
import com.example.remembergroup.chat_app.R;
import com.example.remembergroup.chat_app.SingletonSocket;
import com.example.remembergroup.model.Conversation;
import com.example.remembergroup.model.ListFriends;
import com.example.remembergroup.model.Me;
import com.example.remembergroup.model.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khai Lee on 12/28/2017.
 */

public class RequestAddFriendAdapter extends ArrayAdapter<User> {
    private final String CLIENT_IGNORE_REQUEST_ADDFRIEND  = "CLIENT_IGNORE_REQUEST_ADDFRIEND";
    private final String CLIENT_ACCEPT_REQUEST_ADDFRIEND  = "CLIENT_ACCEPT_REQUEST_ADDFRIEND";

    @NonNull
    private Activity context;
    @LayoutRes
    private int resource;
    @NonNull
    private List<User> objects;

    private List<User> filterData;


    public RequestAddFriendAdapter(@NonNull Activity context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);

        this.filterData=objects;
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    private String listEmailFriendString(){
        ArrayList<String> listEmailFriend = new ArrayList<>();
        for (int i = 0; i < ListFriends.getInstance().getArray().size(); i++){
            listEmailFriend.add(ListFriends.getInstance().getArray().get(i).getEmail());
        }

        Gson gson = new Gson();

        return gson.toJson(listEmailFriend);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get layout inflater
        LayoutInflater inflater=context.getLayoutInflater();
        //ViewHolder holder = null;

        @SuppressLint("ViewHolder")
        View row=inflater.inflate(resource,null);
        TextView txtName = row.findViewById(R.id.txtName);
        ImageView imgAvatar = row.findViewById(R.id.imgAvatar);
        ImageView imgAccept = row.findViewById(R.id.imgAccept);
        ImageView imgCancel = row.findViewById(R.id.imgCancel);

        final User user = filterData.get(position);

        //set values to each controls
        txtName.setText(user.getName());

        if(MemoryManager.getInstance().getBitmapFromMemCache(user.getEmail())!=null)
            imgAvatar.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(user.getEmail()));

        //add events
        imgAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingletonSocket.getInstance().mSocket.emit(CLIENT_ACCEPT_REQUEST_ADDFRIEND, user.getEmail());
                objects.remove(user);
                Me.getInstance().listEUserRequest.remove(user.getEmail());
                Me.getInstance().accepted = user;
                notifyDataSetChanged();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingletonSocket.getInstance().mSocket.emit(CLIENT_IGNORE_REQUEST_ADDFRIEND, user.getEmail());
                objects.remove(user);
                Me.getInstance().listEUserRequest.remove(user.getEmail());
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
