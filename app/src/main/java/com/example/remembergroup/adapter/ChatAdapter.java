package com.example.remembergroup.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remembergroup.chat_app.MemoryManager;
import com.example.remembergroup.chat_app.R;
import com.example.remembergroup.model.Constant;
import com.example.remembergroup.model.Friend;
import com.example.remembergroup.model.ImageMessage;
import com.example.remembergroup.model.Message;
import com.example.remembergroup.model.Me;
import com.example.remembergroup.model.TextMessage;

import java.util.List;

/**
 * Created by chauvansang on 9/23/2017.
 */

public class ChatAdapter extends ArrayAdapter<Message> {
    private Friend friend;
    @NonNull
    private Activity context;
    @NonNull
    private List<Message> objects;

    private ArrayList<Integer> listLayout;
    public ChatAdapter(@NonNull Activity context,
                       @NonNull ArrayList<Integer> listLayout,
                       @NonNull List<Message> objects,
                       Friend friend) {
        super(context, listLayout.get(0), objects);
        this.context=context;
        this.objects=objects;
        this.friend=friend;
        this.listLayout=listLayout;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message =objects.get(position);
        ViewHolder holder = null;
        LayoutInflater inflater=context.getLayoutInflater();

        int layout = listLayout.get(0);
        switch (message.getType())
        {
            case Constant.TYPE_IMAGE_MESSAGE:
                if (message.isMine())
                {
                    layout=listLayout.get(2);
                }
                else
                {
                    layout=listLayout.get(3);
                }
                break;
            case Constant.TYPE_TEXT_MESSAGE:
                if (message.isMine())
                {
                    layout=listLayout.get(0);
                }
                else
                {
                    layout=listLayout.get(1);
                }
                break;
        }
        convertView = inflater.inflate(layout,null);
        holder=new ViewHolder(convertView,message.getType());
        convertView.setTag(holder);


        switch (message.getType())
        {
            case Constant.TYPE_TEXT_MESSAGE:
                makeViewText(inflater,convertView,holder,message);
                break;

            case  Constant.TYPE_IMAGE_MESSAGE:
                makeViewImage(inflater,convertView,holder,message);
                break;
            default:
                break;
        }

        return convertView;
    }


    private void makeViewImage(LayoutInflater inflater,View convertView,ViewHolder holder,Message message)
    {
        ImageMessage imageMessage = (ImageMessage) message;
        if(imageMessage.isMine())
        {
            holder.imgImage.setImageBitmap(imageMessage.getImage());
            @SuppressLint({"NewApi", "LocalSuppress", "SimpleDateFormat"})
            SimpleDateFormat format=new SimpleDateFormat("MMMM dd,yyyy");
            holder.imgAvatar.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(Me.getInstance().getEmail()));
        }
        else
        {
            Calendar calendar=Calendar.getInstance();
            holder.imgImage.setImageBitmap(imageMessage.getImage());
            @SuppressLint({"NewApi", "LocalSuppress", "SimpleDateFormat"})
            SimpleDateFormat format=new SimpleDateFormat("MMMM dd,yyyy");
            holder.txtTime.setText(format.format(calendar.getTime()));
            holder.imgAvatar.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(friend.getEmail()));
        }
    }

    private void makeViewText(LayoutInflater inflater,View convertView,ViewHolder holder,Message message) {
        TextMessage textMessage= (TextMessage) message;
        if(textMessage.isMine())
        {
            holder.txtMessage.setText(textMessage.getText());
            holder.imgAvatar.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(Me.getInstance().getEmail()));
        }
        else
        {
            Calendar calendar=Calendar.getInstance();
            holder.txtMessage.setText(textMessage.getText());
            @SuppressLint({"NewApi", "LocalSuppress", "SimpleDateFormat"})
            SimpleDateFormat format=new SimpleDateFormat("MMMM dd,yyyy");
            holder.txtTime.setText(format.format(calendar.getTime()));
            holder.imgAvatar.setImageBitmap(MemoryManager.getInstance().getBitmapFromMemCache(friend.getEmail()));
        }

    }


    private static class ViewHolder
    {

        private TextView txtMessage;
        private ImageView imgImage;
        private TextView txtTime;
        private ImageView imgAvatar;

        public ViewHolder()
        {

        }

        public ViewHolder(View view,int type) {

            switch (type)
            {
                //case conversation is text
                case Constant.TYPE_TEXT_MESSAGE:
                    txtMessage=view.findViewById(R.id.txtMessage);
                    txtTime=view.findViewById(R.id.txtTime);
                    imgAvatar=view.findViewById(R.id.imgAvatar);
                    break;
                //case conversation is image
                case  Constant.TYPE_IMAGE_MESSAGE:
                    this.imgImage = view.findViewById(R.id.imgImage);
                    txtTime=view.findViewById(R.id.txtTime);
                    imgAvatar=view.findViewById(R.id.imgAvatar);
                    break;
                default:
                    break;
            }

        }

    }
}
