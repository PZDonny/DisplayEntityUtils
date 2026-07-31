package net.donnypz.displayentityutils.utils.DisplayEntities;

public interface ActivePartSelection<T extends ActivePart> extends Active{
    /**
     * Get the part that is currently selected in this selection
     * @return an {@link ActivePart} or null
     */
    T getSelectedPart();

    /**
     * Get whether this part selection has an {@link ActivePart} currently selected
     * @return a boolean
     */
    boolean hasSelectedPart();

    void remove();

    /**
     * Get whether this selection is a {@link SinglePartSelection}
     * @return a boolean
     */
    default boolean isSinglePartSelection(){
        return (this instanceof SinglePartSelection);
    }
}
