package net.donnypz.displayentityutils.utils.DisplayEntities;

public abstract class PartSelection implements Active{
    protected ActivePart selectedPart = null;

    public abstract ActivePart getSelectedPart();

    public abstract void remove();

    /**
     * Get whether this part selection is valid and usable
     * @return a boolean
     */
    public abstract boolean isValid();
}
