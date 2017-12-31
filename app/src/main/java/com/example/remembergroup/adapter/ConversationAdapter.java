package com.example.remembergroup.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.remembergroup.chat_app.ChatActivity;
import com.example.remembergroup.chat_app.MainActivity;
import com.example.remembergroup.chat_app.MemoryManager;
import com.example.remembergroup.chat_app.R;
import com.example.remembergroup.chat_app.SingletonSocket;
import com.example.remembergroup.model.Conversation;
import com.example.remembergroup.model.Friend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chauvansang on 9/21/2017.
 */

public class ConversationAdapter extends ArrayAdapter<Conversation>  {
    private final String CLIENT_REMOVE_CONVERSATION = "CLIENT_REMOVE_CONVERSATION";

    @NonNull
    private Activity context;
    @LayoutRes
    private int resource;
    @NonNull
    private List<Conversation> objects;

    private List<Conversation> filterData;

    public ConversationAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<Conversation> objects) {
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
        TextView txtText=row.findViewById(R.id.txtText);
        TextView txtTime=row.findViewById(R.id.txtTime);
        ImageView imgOnline=row.findViewById(R.id.imgOnline);
        ImageView imgImage=row.findViewById(R.id.imgImage);
        Button btnMenu=row.findViewById(R.id.btnMenu);

        final Conversation con=filterData.get(position);

        //set values to each controls
        txtName.setText(con.getFriend().getName());

        if(MemoryManager.getInstance().getBitmapFromMemCache(con.getFriend().getEmail())!=null)
            imgImage.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(con.getFriend().getEmail()));
        if(con.getFriend().isOnline())
            imgOnline.setVisibility(View.VISIBLE);
        else
            imgOnline.setVisibility(View.INVISIBLE);
        txtText.setText(con.getLastMess());
        txtTime.setText(con.getTimeCreated());

        //add events
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMenuClick(view, con);
            }
        });

        return row;
    }

    private void handleMenuClick(View view, final Conversation con) {
        PopupMenu popupMenu=new PopupMenu(this.context,view);
        MenuInflater inflater=popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.mnuChat){
                    launchChatRoom("CON_CHAT", con, true);
                    return true;
                }

                if (menuItem.getItemId() == R.id.mnuDelete) {
                    SingletonSocket.getInstance().mSocket.emit(CLIENT_REMOVE_CONVERSATION, con.getId());
                    objects.remove(con);
                    notifyDataSetChanged();
                    return true;
                }

                return false;
            }
        });
    }

    // Launch chatRoom activity
    private void launchChatRoom(String key, Conversation cons, boolean isExist){
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(key, cons);
        i.putExtra("isExist", isExist);
        context.startActivity(i);
    }




    @Override
    public int getCount() {
        return filterData.size();
    }


    @Nullable
    @Override
    public Conversation getItem(int position) {
        return filterData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();


                ArrayList<Conversation> cons=new ArrayList<>();
                Conversation temp;
                final FilterResults result = new FilterResults();
                for(int i=0;i<objects.size();i++)
                {
                    temp=objects.get(i);
                    if(temp.getFriend().getName().toLowerCase().contains(filterString))
                    {
                        cons.add(objects.get(i));
                    }
                }
                result.count=cons.size();
                result.values=cons;
                return result;
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filterData= (List<Conversation>) filterResults.values;
                    notifyDataSetChanged();
            }
        };
    }
}
