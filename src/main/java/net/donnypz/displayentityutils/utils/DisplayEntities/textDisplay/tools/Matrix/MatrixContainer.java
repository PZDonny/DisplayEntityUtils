package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix;

import java.util.function.Consumer;

public abstract class MatrixContainer<T> {
    protected boolean isStrict;

    public abstract void set(MatrixCords cords, T value);
    public abstract void clearSlot(MatrixCords cords);
    public abstract void forEach(Consumer<T> action);
    public abstract void clean();
    public abstract MatrixContainer clone();

    public boolean isStrict() { return isStrict; }
    public void setStrict(boolean strict) { this.isStrict = strict; }
}
