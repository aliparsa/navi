package com.graphhopper.android.Adapter;

/**
 * Created by aliparsa on 10/25/2014.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.graphhopper.android.DataModel.Message;
import com.graphhopper.android.R;


import com.graphhopper.android.DataModel.AdapterInputType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliparsa on 8/5/2014.
 */
public class ListViewCustomAdapter<T> extends ArrayAdapter<T> {

    public static String MESSAGE_ITEM = "message item";




    public List<T> itemList;
    Context context;
    public int layoutResID;
    String IMAGE_DRAWER_ITEM = "image";
    String FOOTER = "footer";

    Object tag;


    // main linear layout in view

    LinearLayout ll_message;


    public ListViewCustomAdapter(Context context, int layoutResourceID,
                                 ArrayList<T> itemList) {
        super(context, layoutResourceID, itemList);

        this.context = context;

        this.itemList = itemList;
        this.layoutResID = layoutResourceID;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        DrawerItemHolder holder;


        if (view != null) {
            holder = (DrawerItemHolder) view.getTag();
        } else {
            holder = new DrawerItemHolder();


            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);

//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.activity_fade_in_animation);
//            animation.setStartOffset(20 * (position + 1));

            ll_message = (LinearLayout) view.findViewById(R.id.ll_message);
        }


        T item = itemList.get(position);

        Message messageItem;
        if (item instanceof Message) {
             messageItem = (Message) item;
            getMessageItem(holder, messageItem);
            OnlyShow(ll_message);
        }

        view.setTag(holder);


        return view;
    }

    public void OnlyShow(LinearLayout lv) {
        lv.setVisibility(LinearLayout.VISIBLE);
    }


    private void getMessageItem(DrawerItemHolder holder, Message item) {

        if (holder.messageTitle == null)
            holder.messageTitle = (TextView) ll_message.findViewById(R.id.message_title);

        if (holder.messageSubtitle == null)
            holder.messageSubtitle = (TextView) ll_message.findViewById(R.id.message_subtitle);


        holder.messageTitle.setText(item.getX2());
        holder.messageSubtitle.setText(item.getX3());



    }



    public static class DrawerItemHolder {


        // Global -------------------------
        private Object tag;

        // Message Item ------------
        TextView messageTitle;
        TextView messageSubtitle;
        TextView messageDate;
        ///------------------------

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }
    }

}
