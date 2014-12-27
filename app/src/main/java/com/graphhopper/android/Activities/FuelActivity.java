package com.graphhopper.android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.graphhopper.android.Adapter.ListViewCustomAdapter;
import com.graphhopper.android.DataModel.Fuel;
import com.graphhopper.android.Helpers.DatabaseHelper;
import com.graphhopper.android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FuelActivity extends Activity {

    private Context context;
    private ListView lv;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);


        context= this;
        lv = (ListView) findViewById(R.id.listView);

        LoadFuel(null);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Fuel fuel = (Fuel) ((ListViewCustomAdapter.DrawerItemHolder) view.getTag()).getTag();
                //showMessageAsDialog(message);
                Toast.makeText(context,fuel.getLiter(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void LoadFuel(String key) {

        DatabaseHelper db = new DatabaseHelper(context);
        ArrayList<Fuel> fuels= db.getAllFuel(key);


            ListViewCustomAdapter adapter = new ListViewCustomAdapter(context, 0, fuels);
            lv.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fuel, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                LoadFuel(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                LoadFuel(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId()==R.id.fuel_add){

           final AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("چند لیتر سوختگیری نموده اید؟");

// Set up the input
           final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
           input.setInputType(InputType.TYPE_CLASS_NUMBER );
           builder.setView(input);

// Set up the buttons
           builder.setPositiveButton("ذخیره", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                      DatabaseHelper db = new DatabaseHelper(context);
                   Calendar calendar = Calendar.getInstance();

                   Date d = new Date(System.currentTimeMillis());
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh-mm");
                   String date = sdf.format(d);
                   db.insertFuel(input.getText().toString(), date);
                   Toast.makeText(context,"ذخیره شد",Toast.LENGTH_SHORT).show();
                   LoadFuel(null);
               }
           });
           builder.setNegativeButton("لغو", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.cancel();
               }
           });

           builder.show();

       }
        return super.onOptionsItemSelected(item);
    }
}
