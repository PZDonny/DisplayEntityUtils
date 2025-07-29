package net.donnypz.displayentityutils.utils.DisplayEntities;

public abstract class PartSelection<T extends ActivePart> implements Active{

    Class<T> partClass;
    protected T selectedPart = null;

    PartSelection(Class<T> partClass){
        this.partClass = partClass;
    }

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
}
