package net.donnypz.displayentityutils.utils.DisplayEntities;

public abstract class ActivePartSelection<T extends ActivePart> implements Active{

    protected T selectedPart = null;

    /**
     * Get the part that is currently selected in this selection
     * @return an {@link ActivePart} or null
     */
    public T getSelectedPart(){
        return selectedPart;
    }

    /**
     * Get whether this part selection has an {@link ActivePart} currently selected
     * @return a boolean
     */
    public boolean hasSelectedPart(){
        return selectedPart != null;
    }

    public abstract void remove();

    /**
     * Get whether this part selection is valid and usable
     * @return a boolean
     */
    public abstract boolean isValid();

    public boolean isSinglePartSelection(){
        return (this instanceof SinglePartSelection);
    }
}
