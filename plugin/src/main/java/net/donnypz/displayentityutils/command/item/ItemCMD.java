package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.command.*;

public final class ItemCMD extends ParentSubCommand {

    public ItemCMD(){
        super("item");
        new ItemSetCMD(this);
        new ItemToggleGlintCMD(this);
        new ItemTransformCMD(this);
    }

}
