package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.command.*;

public final class BDEngineCMD extends ParentSubCommand{

    static final String DESPAWN_FLAG = "-despawn";
    static final String ADAPT_TAGS_FLAG = "-adapttags";

    public BDEngineCMD(){
        super("bdengine");
        new BDEngineConvertDatapackCMD(this);
        new BDEngineImportCMD(this);
        new BDEngineSpawnModelCMD(this);
    }
}
