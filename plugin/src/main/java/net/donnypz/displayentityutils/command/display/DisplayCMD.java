package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.command.*;

public final class DisplayCMD extends ParentSubCommand {


    public DisplayCMD(){
        super("display");
        new DisplayGlowColorCMD(this);
        new DisplayBrightnessCMD(this);
        new DisplayViewRangeCMD(this);
        new DisplayBillboardCMD(this);
        new DisplayTranslateCMD(this);
        new DisplayResetCMD(this);
        new DisplayRotateCMD(this);
        new DisplaySetRotationCMD(this);
        new DisplayScaleCMD(this);
        new DisplaySetBlockCMD(this);
    }

}
