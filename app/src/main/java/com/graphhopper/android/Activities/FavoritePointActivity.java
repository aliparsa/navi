package com.graphhopper.android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.graphhopper.android.Adapter.ListViewCustomAdapter;
import com.graphhopper.android.DataModel.FavoritePoint;
import com.graphhopper.android.DataModel.Message;
import com.graphhopper.android.Helpers.DatabaseHelper;
import com.graphhopper.android.R;

import java.util.ArrayList;

public class FavoritePointActivity extends Activity {

    private Context context;
    ListView lv;
    ArrayList<FavoritePoint> allFavoritePoint;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        context = this;
        lv = (ListView) findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0)return;

                if(i>0){
                    FavoritePoint favoritePoint = (FavoritePoint) ((ListViewCustomAdapter.DrawerItemHolder) view.getTag()).getTag();
                    Toast.makeText(context, favoritePoint.getDescription(), Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(context)
                            .setTitle("نقطه مکان")
                            .setMessage(favoritePoint.getX1() + "\n" + favoritePoint.getLon() + "\n")
                            .setPositiveButton("نمایش روی نقشه", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton("مسیر یابی به اینجا", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }

                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }



            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(context)
                        .setSingleChoiceItems(new String[]{"حذف"}, 0, null)
                        .setPositiveButton("انجام", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                // Do something useful withe the position of the selected radio button
                            }
                        })
                        .show();
                return true;
            }
        });



        loadAndShowFavoritePoints(null);

    }



    public void loadAndShowFavoritePoints(String key){
        DatabaseHelper db = new DatabaseHelper(context);
        ArrayList<FavoritePoint> allFavoritePoint = db.getAllFavoritePoint(key);


            ListViewCustomAdapter adapter = new ListViewCustomAdapter(context, 0, allFavoritePoint);
            lv.setAdapter(adapter);
            searchView.requestFocus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.private_poi, menu);
        searchView = (SearchView) menu.findItem(R.id.po_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                loadAndShowFavoritePoints(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadAndShowFavoritePoints(s);
                return false;
            }
        });

        return true;
    }

}
