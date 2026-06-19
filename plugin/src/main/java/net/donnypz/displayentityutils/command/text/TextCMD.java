package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.command.*;

public final class TextCMD extends ParentSubCommand {

    public TextCMD(){
        super("text");
        new TextEditCMD(this);
        new TextSetCMD(this);
        new TextAddLineCMD(this);
        new TextFontCMD(this);
        new TextShadowCMD(this);
        new TextSeeThroughCMD(this);
        new TextAlignCMD(this);
        new TextLineWidthCMD(this);
        new TextBackgroundCMD(this);
        new TextOpacityCMD(this);
    }

}
