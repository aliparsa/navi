package com.graphhopper.android.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.graphhopper.android.Adapter.ListViewCustomAdapter;
import com.graphhopper.android.DataModel.AdapterInputType;
import com.graphhopper.android.DataModel.Message;
import com.graphhopper.android.Helpers.DatabaseHelper;
import com.graphhopper.android.R;

import java.util.ArrayList;

public class MessagingActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        context= this;

        DatabaseHelper db = new DatabaseHelper(context);
        ArrayList<Message> messages = db.getAllMessages();
        if (messages.size()>0) {
            ListView lv = (ListView) findViewById(R.id.listView);
            ListViewCustomAdapter adapter = new ListViewCustomAdapter(context, 0, messages);
            lv.setAdapter(adapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messaging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
