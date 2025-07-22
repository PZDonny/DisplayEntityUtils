package net.donnypz.displayentityutils.utils.DisplayEntities;

public abstract class DisplaySelection implements Active{
    protected ActivePart selectedPart = null;

    public abstract ActivePart getSelectedPart();

    public abstract void remove();
}
