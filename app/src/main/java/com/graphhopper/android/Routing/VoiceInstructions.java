package com.graphhopper.android.Routing;

import com.graphhopper.android.R;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class VoiceInstructions {

    public static int getVoiceFromSign(int sign) {
        switch (sign) {
            case -3:
                return R.raw.left_sh;
            case -2:
                return R.raw.left;
            case -1:
                return R.raw.left_sl;
            case 0:
                return R.raw.go_ahead;
            case 1:
                return R.raw.right_sl;
            case 2:
                return R.raw.right;
            case 3:
                return R.raw.right_sh;
            case 4:
                return R.raw.reached_destination;
//            case 5:
//                return "REACHED_VIA";

        }
        return 0;
    }

    public static int getVoiceId(int meter) {

        if (meter == 1) return R.raw._1;
        if (meter == 2) return R.raw._2;
        if (meter == 3) return R.raw._3;
        if (meter == 4) return R.raw._4;
        if (meter == 5) return R.raw._5;
        if (meter == 6) return R.raw._6;
        if (meter == 7) return R.raw._7;
        if (meter == 8) return R.raw._8;
        if (meter == 9) return R.raw._9;
        if (meter == 10) return R.raw._10;
        if (meter == 11) return R.raw._11;
        if (meter == 12) return R.raw._12;
        if (meter == 13) return R.raw._13;
        if (meter == 14) return R.raw._14;
        if (meter == 15) return R.raw._15;
        if (meter == 16) return R.raw._16;
        if (meter == 17) return R.raw._17;
        if (meter == 18) return R.raw._18;
        if (meter == 19) return R.raw._19;
        if (meter > 19 && meter <= 20) return R.raw._20;
        if (meter > 20 && meter <= 25) return R.raw._25;
        if (meter > 25 && meter <= 30) return R.raw._30;
        if (meter > 30 && meter <= 35) return R.raw._35;
        if (meter > 35 && meter <= 40) return R.raw._40;
        if (meter > 40 && meter <= 45) return R.raw._45;
        if (meter > 45 && meter <= 50) return R.raw._50;
        if (meter > 50 && meter <= 55) return R.raw._55;
        if (meter > 55 && meter <= 60) return R.raw._60;
        if (meter > 60 && meter <= 65) return R.raw._65;
        if (meter > 65 && meter <= 70) return R.raw._70;
        if (meter > 70 && meter <= 75) return R.raw._75;
        if (meter > 75 && meter <= 80) return R.raw._80;
        if (meter > 80 && meter <= 85) return R.raw._85;
        if (meter > 85 && meter <= 90) return R.raw._90;
        if (meter > 90 && meter <= 95) return R.raw._95;
        if (meter > 95 && meter <= 100) return R.raw._100;
        if (meter > 100 && meter <= 150) return R.raw._150;
        if (meter > 150 && meter <= 200) return R.raw._200;

        if (meter > 200 && meter <= 250) return R.raw._250;
        if (meter > 250 && meter <= 300) return R.raw._300;
        if (meter > 300 && meter <= 350) return R.raw._350;
        if (meter > 350 && meter <= 400) return R.raw._400;
        if (meter > 400 && meter <= 450) return R.raw._450;
        if (meter > 450 && meter <= 500) return R.raw._500;
        if (meter > 500 && meter <= 550) return R.raw._550;
        if (meter > 550 && meter <= 600) return R.raw._600;
        if (meter > 600 && meter <= 650) return R.raw._650;
        if (meter > 650 && meter <= 700) return R.raw._700;
        if (meter > 700 && meter <= 750) return R.raw._750;
        if (meter > 750 && meter <= 800) return R.raw._800;
        if (meter > 800 && meter <= 850) return R.raw._850;
        if (meter > 850 && meter <= 900) return R.raw._900;

        if (meter > 900 && meter <= 1000) return R.raw._1000;
        if (meter > 1000 && meter <= 2000) return R.raw._2000;
        if (meter > 2000 && meter <= 3000) return R.raw._3000;
        if (meter > 3000 && meter <= 4000) return R.raw._4000;
        if (meter > 4000 && meter <= 5000) return R.raw._5000;
        if (meter > 5000 && meter <= 6000) return R.raw._6000;
        if (meter > 6000 && meter <= 7000) return R.raw._7000;
        if (meter > 7000 && meter <= 8000) return R.raw._8000;
        if (meter > 8000 && meter <= 9000) return R.raw._9000;


        return R.raw._1;
    }
}
