package com.graphhopper.android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.graphhopper.android.Adapter.ListViewCustomAdapter;
import com.graphhopper.android.DataModel.AdapterInputType;
import com.graphhopper.android.DataModel.Message;
import com.graphhopper.android.Helpers.DatabaseHelper;
import com.graphhopper.android.Helpers.DrawOnMapHelper;
import com.graphhopper.android.Helpers.MapViewHelper;
import com.graphhopper.android.R;

import java.util.ArrayList;

public class MessagingActivity extends Activity {

    private Context context;
    ListView lv;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        context= this;
        lv = (ListView) findViewById(R.id.listView);

        searchView = new SearchView(context);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                loadAndShowMessages(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadAndShowMessages(s);
                return false;
            }
        });
        lv.addHeaderView(searchView);

        loadAndShowMessages(null);





        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0)return;

                Message message = (Message) ((ListViewCustomAdapter.DrawerItemHolder) view.getTag()).getTag();
                showMessageAsDialog(message);
                //Toast.makeText(context,message.getX1(),Toast.LENGTH_SHORT).show();
            }
        });




        }


    private void loadAndShowMessages(String key) {
        DatabaseHelper db = new DatabaseHelper(context);
        ArrayList<Message> messages = db.getAllMessages(key);
        if (messages.size()>0) {

            ListViewCustomAdapter adapter = new ListViewCustomAdapter(context, 0, messages);
            lv.setAdapter(adapter);
            searchView.requestFocus();

    }}

    private void showMessageAsDialog(Message message) {
        new AlertDialog.Builder(this)
                .setTitle(message.getX1())
                .setMessage(message.getX2())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }





}
