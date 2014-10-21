package com.graphhopper.android.DataModel;

import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;

/**
 * Created by aliparsa on 10/14/2014.
 */
public class VoiceFlags {

    public boolean over100m=false;
    public boolean over10m=false;
    public boolean under10m=false;
    public boolean reachedDestination=false;
    Instruction instruction;

    public VoiceFlags(Instruction instruction){
        this.instruction=instruction;
    }


    public void calculate(Instruction instruction) {
        if ((instruction.getPoints().getLat(instruction.getPoints().size()-1)!= this.instruction.getPoints().getLat(this.instruction.getPoints().size()-1)) ||  (instruction.getPoints().getLon(instruction.getPoints().size()-1)!= this.instruction.getPoints().getLon(this.instruction.getPoints().size()-1))){
            over100m=over10m=under10m=reachedDestination=false;
            this.instruction=instruction;
    }
    }

    public void clear() {
        over100m=over10m=under10m=reachedDestination=false;
    }
}
