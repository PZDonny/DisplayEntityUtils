package net.donnypz.displayentityutils.utils.DisplayEntities;

public abstract class PartSelection implements Active{
    protected ActivePart selectedPart = null;

    public abstract ActivePart getSelectedPart();

    public abstract void remove();
}
