package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix;

public class MatrixCords {
    private int x;
    private int y;
    private transient MatrixContainer matrix; // Using the generic abstract MatrixContainer
    private transient Object object;


    public MatrixCords(int x, int y, MatrixContainer matrix, Object object) {
        this.x = x;
        this.y = y;
        this.object = object;
        migrateMatrix(matrix);
        matrix.set(this, object);
    }
    public MatrixCords(int x, int y, Object object) {
        this.x = x;
        this.y = y;
        this.object = object;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public void migrateMatrix(MatrixContainer newMatrix) {
        if (newMatrix == matrix) return;

        if (matrix != null) {
            matrix.clearSlot(this);
        }
        newMatrix.set(this,object);
        matrix = newMatrix;
    }

    /**
     * Overrides current coordinates with newCords and updates the matrix with the given object.
     * Only allows override if the types match exactly.
     */
    public void override(MatrixCords newCords, Object object) {
        if (newCords == null || object == null) return;

        if (!newCords.object.getClass().equals(object.getClass())) return;

        migrateMatrix(newCords.matrix);
        this.x = newCords.getX();
        this.y = newCords.getY();
        this.object = object;
        matrix.set(this, object);
    }

    public void remove() {
        if (matrix != null) {
            matrix.clearSlot(this);
        }
    }


    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        if (object.getClass()==this.object.getClass()) {
            matrix.set(this,object);
            this.object = object;
        }
    }
}
