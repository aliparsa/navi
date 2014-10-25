package com.graphhopper.android.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graphhopper.GHResponse;
import com.graphhopper.android.Helpers.DrawOnMapHelper;
import com.graphhopper.android.R;
import com.graphhopper.util.Instruction;

import org.mapsforge.map.android.view.MapView;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class TextInstructionsHelper {
    public static void createAndShowInstructionList(Context context,GHResponse lastRouteResponce, LinearLayout llDirectionList, final MapView mapView, final int layerCount) {
        llDirectionList.removeAllViews();
        for (int i = 0; i < lastRouteResponce.getInstructions().size(); i++) {

            final Instruction instruction = lastRouteResponce.getInstructions().get(i);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            // inflater =  getLayoutInflater();
            RelativeLayout llDirection_item = (RelativeLayout) inflater.inflate(R.layout.direction_item, null);
            ImageView img = (ImageView) llDirection_item.findViewById(R.id.imageView);
            TextView txt = (TextView) llDirection_item.findViewById(R.id.textView);

            String insName = instruction.getName();
            int insDistance = (int) instruction.getDistance();
            int insSign = (int) instruction.getSign();


            // if (insDistance==0 && insSign !=4) continue;


            switch (insSign) {
                case -3:
                    txt.setText(" پیچ تند به سمت چپ ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.hardleft);
                    break;
                case -2:
                    txt.setText(" پیچ به سمت چپ ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.left);
                    break;
                case -1:
                    txt.setText("پیچ ملایم به سمت چپ ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.light_left);
                    break;
                case 0:
                    img.setImageResource(R.drawable._continue);
                    if (insName.length() > 0)
                        txt.setText(" در  " + insName + "\n" + insDistance + " متر " + " ادامه دهید ");
                    else
                        txt.setText(" در همین خیابان " + "\n" + insDistance + " متر " + " ادامه دهید ");
                    break;
                case 1:
                    txt.setText(" پیچ ملایم به سمت راست ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.light_right);
                    break;
                case 2:
                    txt.setText(" پیچ  به سمت راست ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.right);
                    break;
                case 3:
                    txt.setText(" پیچ تند به سمت راست ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.hardright);
                    break;
                case 4:
                    txt.setText(" به مقصد میرسید ");
                    img.setImageResource(R.drawable.destination);
                    break;
            }

            llDirection_item.setTag(instruction);
            llDirection_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DrawOnMapHelper.drawPolylineByPoints(mapView,layerCount,instruction.getPoints());
                }
            });
            llDirectionList.addView(llDirection_item);

        }
    }
}
