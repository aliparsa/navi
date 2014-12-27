package com.graphhopper.android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.graphhopper.android.DataModel.Setting;
import com.graphhopper.android.R;
import com.graphhopper.android.utilities.Webservice;

public class SettingActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        context=this;
        Webservice.init(context);

        final Setting setting = new Setting(context);

        Switch voiceSwitch = (Switch) findViewById(R.id.switch1);
        Switch taximeterSwitch = (Switch) findViewById(R.id.switch2);
        Switch textSwitch = (Switch) findViewById(R.id.switch3);





        LinearLayout ll_server_address = (LinearLayout) findViewById(R.id.ll_server_address);
        final TextView server_address = (TextView) findViewById(R.id.current_server_address);

        server_address.setText(Webservice.getServerAddress());
        ll_server_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("آدرس سرور");

// Set up the input
                final EditText input = new EditText(context);
                input.setText(Webservice.getServerAddress());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("ذخیره", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Webservice.modifyServerAddress(input.getText().toString(), context);
                        server_address.setText(Webservice.getServerAddress());
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
        });



        voiceSwitch.setChecked(setting.getVoiceInstructionStatus());
        textSwitch.setChecked(setting.getTextInstructionStatus());
        taximeterSwitch.setChecked(setting.getTaximeterStatus());


        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setting.setVoiceInstruction(b);
            }
        });


        textSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setting.setTextInstruction(b);
            }
        });


        taximeterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setting.setTaximeter(b);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
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
