package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

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
     * Get whether this selection is a {@link SinglePartSelection}
     * @return a boolean
     */
    public boolean isSinglePartSelection(){
        return (this instanceof SinglePartSelection);
    }

    /**
     * Get the location of the part(s) represented in this selection
     * @return a {@link Location} or null if the selection is invalid
     */
    public abstract @Nullable Location getLocation();
}
