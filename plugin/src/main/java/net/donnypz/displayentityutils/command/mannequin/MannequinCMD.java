package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.command.*;

public class MannequinCMD extends ParentSubCommand {
    public MannequinCMD() {
        super("mannequin");
        new MannequinSpawnCMD(this);
        new MannequinNameCMD(this);
        new MannequinUnnameCMD(this);
        new MannequinBelowNameCMD(this);
        new MannequinToggleNameVisibilityCMD(this);
        new MannequinSkinCMD(this);
        new MannequinPoseCMD(this);
        new MannequinPivotCMD(this);
        new MannequinScaleCMD(this);
        new MannequinToggleGravityCMD(this);
        new MannequinToggleImmovableCMD(this);
        new MannequinMainHandCMD(this);
        new MannequinEquipmentCMD(this);
    }
}
