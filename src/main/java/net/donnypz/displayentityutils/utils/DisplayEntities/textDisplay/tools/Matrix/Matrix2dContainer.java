package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix;

import net.donnypz.displayentityutils.DisplayEntityPlugin;

import java.lang.reflect.Array;
import java.util.function.Consumer;


public class Matrix2dContainer<T> extends MatrixContainer<T> {

    private final Class<T> type;
    private int quadrantInitSize = 16;            // initial size per side (rows/cols)
    private int quadrantMinSize = 4;             // never shrink below this

    private T[][] posPos; // (+,+)
    private T[][] negPos; // (-,+)
    private T[][] posNeg; // (+,-)
    private T[][] negNeg; // (-,-)

    // ──────────────────────────── ctor ────────────────────────────
    @SuppressWarnings("unchecked")
    public Matrix2dContainer(Class<T> type) {
        this.type = type;
        posPos = (T[][]) Array.newInstance(type, quadrantInitSize, quadrantInitSize);
        negPos = (T[][]) Array.newInstance(type, quadrantInitSize, quadrantInitSize);
        posNeg = (T[][]) Array.newInstance(type, quadrantInitSize, quadrantInitSize);
        negNeg = (T[][]) Array.newInstance(type, quadrantInitSize, quadrantInitSize);
    }

    // ───────────────────────── Set / Get / Clear ─────────────────────────
    @Override
    public Matrix2dContainer clone(){
        Matrix2dContainer<T> newMatrix = new Matrix2dContainer<>(type);

        this.forEach(new TriConsumer<Integer, Integer, T>() {
            @Override
            public void accept(Integer x, Integer y, T object) {
                if (object==null){
                    return;
                }
                newMatrix.set(x,y,object);
            }
        });

        return newMatrix;
    }
    @Override
    public void set(MatrixCords cords, T value) {
        set(cords.getX(), cords.getY(), value);
    }

    public void set(int x, int y, T value) {
        if (value == null) return;
        T[][] quad = getOrCreateQuadrant(x, y);
        int ix = toIdx(x);
        int iy = toIdx(y);
        quad = ensureCapacity(quad, iy + 1, ix + 1, x, y);  // assign back!

        if (iy >= quad.length || ix >= quad[0].length) {
            DisplayEntityPlugin.getInstance().getLogger()
                    .info("ERROR " + ix + " " + iy + " BOUNDS " + quad.length + " " + quad[0].length);
            return;
        }
        quad[iy][ix] = value;
    }


    @Override
    public void clearSlot(MatrixCords cords) {
        clearSlot(cords.getX(), cords.getY());
    }

    public void clearSlot(int x, int y) {
        T[][] quad = getQuadrant(x, y);
        if (quad == null) return;
        int ix = toIdx(x);
        int iy = toIdx(y);
        if (iy < quad.length && ix < quad[0].length) quad[iy][ix] = null;
    }

    public T get(int x, int y) {
        T[][] quad = getQuadrant(x, y);
        if (quad == null) return null;
        int ix = toIdx(x);
        int iy = toIdx(y);
        return (iy < quad.length && ix < quad[0].length) ? quad[iy][ix] : null;
    }

    public T get(MatrixCords cords) {
        return get(cords.getX(), cords.getY());
    }

    // ─────────────────────────── Iteration ──────────────────────────

    public void forEach(TriConsumer<Integer, Integer, T> action) {
        walkQuadrant(posPos, 1, 1, action);
        walkQuadrant(negPos, -1, 1, action);
        walkQuadrant(posNeg, 1, -1, action);
        walkQuadrant(negNeg, -1, -1, action);
    }

    @Override
    public void forEach(Consumer<T> action) {
        forEach((x, y, v) -> action.accept(v));
    }

    private void walkQuadrant(T[][] quad, int xs, int ys, TriConsumer<Integer, Integer, T> f) {
        if (quad == null) return;
        for (int r = 0; r < quad.length; r++) {
            for (int c = 0; c < quad[r].length; c++) {
                T v = quad[r][c];
                if (v == null) continue;
                int wx = xs > 0 ? c : -c - 1;
                int wy = ys > 0 ? r : -r - 1;
                f.accept(wx, wy, v);
            }
        }
    }

    // ─────────────────────── Internal helpers ───────────────────────

    private int toIdx(int coord) {
        return coord >= 0 ? coord : -coord - 1;
    }

    private T[][] getQuadrant(int x, int y) {
        if (x >= 0 && y >= 0) return posPos;
        if (x < 0 && y >= 0) return negPos;
        if (x >= 0 && y < 0) return posNeg;
        return negNeg;
    }

    @SuppressWarnings("unchecked")
    private T[][] getOrCreateQuadrant(int x, int y) {
        T[][] quad = getQuadrant(x, y);
        if (quad != null) return quad;
        quad = (T[][]) Array.newInstance(type, quadrantInitSize, quadrantInitSize);
        if (x >= 0 && y >= 0) posPos = quad;
        else if (x < 0 && y >= 0) negPos = quad;
        else if (x >= 0) posNeg = quad;
        else negNeg = quad;
        return quad;
    }


    @SuppressWarnings("unchecked")
    private T[][] ensureCapacity(T[][] quad, int r, int c, int x, int y) {
        int rows = quad.length, cols = quad[0].length;
        boolean grow = false;
        while (rows < r) { rows *= 2; grow = true; }
        while (cols < c) { cols *= 2; grow = true; }
        if (!grow) return quad;

        T[][] bigger = (T[][]) Array.newInstance(type, rows, cols);
        for (int i = 0; i < quad.length; i++)
            System.arraycopy(quad[i], 0, bigger[i], 0, quad[i].length);

        if (x >= 0 && y >= 0) posPos = bigger;
        else if (x < 0 && y >= 0) negPos = bigger;
        else if (x >= 0 && y < 0) posNeg = bigger;
        else negNeg = bigger;

        return bigger;
    }

    // ─────────────────────────── Shrinking ───────────────────────────
    @Override public void clean(){
        shrinkQuadrant(posPos,(q)->posPos=q);
        shrinkQuadrant(negPos,(q)->negPos=q);
        shrinkQuadrant(posNeg,(q)->posNeg=q);
        shrinkQuadrant(negNeg,(q)->negNeg=q);
    }

    @SuppressWarnings("unchecked")
    private void shrinkQuadrant(T[][] quad, Consumer<T[][]> setter) {
        if (quad == null) return;
        int rows = quad.length, cols = quad[0].length;
        int maxRow = -1, maxCol = -1;
        outer:
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = 0; c < cols; c++) {
                if (quad[r][c] != null) {
                    maxRow = r;
                    break outer;
                }
            }
        }
        outer:
        for (int c = cols - 1; c >= 0; c--) {
            for (int r = 0; r < rows; r++) {
                if (quad[r][c] != null) {
                    maxCol = c;
                    break outer;
                }
            }
        }
        if (maxRow == -1) { // quadrant empty
            int newSize = Math.max(quadrantMinSize, quadrantInitSize / 2);
            T[][] small = (T[][]) Array.newInstance(type, newSize, newSize);
            setter.accept(small);
            return;
        }
        int neededRows = maxRow + 1, neededCols = maxCol + 1;
        // Apply 75% rule: only shrink if used area is less than 25%
        if (neededRows > rows * 0.25 || neededCols > cols * 0.25) return;
        int newRows = Math.max(quadrantMinSize, nextPowerOfTwo(neededRows));
        int newCols = Math.max(quadrantMinSize, nextPowerOfTwo(neededCols));
        if (newRows == rows && newCols == cols) return;
        T[][] smaller = (T[][]) Array.newInstance(type, newRows, newCols);
        for (int i = 0; i < neededRows; i++) {
            System.arraycopy(quad[i], 0, smaller[i], 0, neededCols);
        }
        setter.accept(smaller);
    }

    private int nextPowerOfTwo(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    // ───────────────────── public helpers / info ─────────────────────

    public int getQuadrantInitSize() {
        return quadrantInitSize;
    }

    public void setQuadrantInitSize(int s) {
        this.quadrantInitSize = Math.max(quadrantMinSize, s);
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}