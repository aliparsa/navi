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

import com.graphhopper.android.DataModel.FavoritePoint;
import com.graphhopper.android.DataModel.Fuel;
import com.graphhopper.android.DataModel.Message;
import com.graphhopper.android.R;


import com.graphhopper.android.DataModel.AdapterInputType;

import org.mapsforge.map.rendertheme.renderinstruction.Line;

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
    LinearLayout ll_favorite_point;
    LinearLayout ll_fuel;


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
            ll_favorite_point = (LinearLayout) view.findViewById(R.id.ll_FavoritePoint);
            ll_fuel = (LinearLayout) view.findViewById(R.id.ll_fuel);
        }


        T item = itemList.get(position);


        Message messageItem;
        if (item instanceof Message) {
            messageItem = (Message) item;
            holder.setTag(messageItem);
            getMessageItem(holder, messageItem);
            OnlyShow(ll_message);
        }



        FavoritePoint favoritePoint;
        if (item instanceof FavoritePoint) {
            favoritePoint = (FavoritePoint) item;
            holder.setTag(favoritePoint);
            getFavoritePointItem(holder, favoritePoint);
            OnlyShow(ll_favorite_point);
        }


        Fuel fuel;
        if (item instanceof Fuel) {
            fuel = (Fuel) item;
            holder.setTag(fuel);
            getFuelItem(holder, fuel);
            OnlyShow(ll_fuel);
        }


        view.setTag(holder);


        return view;
    }

    public void OnlyShow(LinearLayout lv) {
        ll_message.setVisibility(View.GONE);
        ll_favorite_point.setVisibility(View.GONE);
        ll_fuel.setVisibility(View.GONE);

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


    private void getFavoritePointItem(DrawerItemHolder holder, FavoritePoint item) {

        if (holder.favoritePointTitle == null)
            holder.favoritePointTitle = (TextView) ll_favorite_point.findViewById(R.id.favoritePoint_title);

        if (holder.favoritePointSubtitle == null)
            holder.favoritePointSubtitle = (TextView) ll_favorite_point.findViewById(R.id.favoritePoint_subtitle);


        holder.favoritePointTitle.setText(item.getDescription());
        holder.favoritePointSubtitle.setText(item.getDescription());
    }


    private void getFuelItem(DrawerItemHolder holder, Fuel item) {

        if (holder.fuelTitle == null)
            holder.fuelTitle = (TextView) ll_fuel.findViewById(R.id.fuel_title);

        if (holder.fuelSubtitle == null)
            holder.fuelSubtitle = (TextView) ll_fuel.findViewById(R.id.fuel_subtitle);


        holder.fuelTitle.setText(item.getLiter()+" لیتر ");
        holder.fuelSubtitle.setText(" در تاریخ "+item.getDate());
    }



    public static class DrawerItemHolder {


        // Global -------------------------
        private Object tag;

        // Message Item ------------
        TextView messageTitle;
        TextView messageSubtitle;

        // FavoritePoint Item ------------
        TextView favoritePointTitle;
        TextView favoritePointSubtitle;

        // FavoritePoint Item ------------
        TextView fuelTitle;
        TextView fuelSubtitle;



        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }
    }

}
