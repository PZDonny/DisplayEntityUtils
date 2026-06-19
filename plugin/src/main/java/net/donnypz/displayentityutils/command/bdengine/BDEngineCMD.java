package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.command.*;

public final class BDEngineCMD extends ParentSubCommand{

    public BDEngineCMD(){
        super("bdengine");
        new BDEngineConvertDatapackCMD(this);
        new BDEngineImportCMD(this);
        new BDEngineSpawnModelCMD(this);
    }
}
