package mc.recraftors.blahaj.item.nbt;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ContainedNbtLongArray extends NbtLongArray implements ContainedNbtElement<NbtLongArray> {
    @NotNull private final NbtLongArray containedArray;
    @NotNull private final Set<Consumer<ContainedNbtElement<?>>> dirtyListeners;
    private boolean modified;
    private boolean busy;

    @SuppressWarnings("UseBulkOperation")
    public ContainedNbtLongArray(NbtLongArray nbtLongArray, Consumer<ContainedNbtElement<?>>... listeners) {
        super(new long[0]);
        this.containedArray = nbtLongArray;
        this.dirtyListeners = new HashSet<>();
        this.modified = false;
        this.busy = false;
        Arrays.stream(listeners).forEach(this.dirtyListeners::add);
    }

    @Override
    public void dirty() {
        this.modified = true;
        if (this.busy) return;
        this.busy = true;
        this.dirtyListeners.forEach(listener -> listener.accept(this));
        this.busy = false;
    }

    @Override
    public void clean() {
        this.modified = false;
    }

    @Override
    public NbtLongArray getContained() {
        return this.containedArray;
    }

     @Override
    public boolean isDirty() {
        return this.modified;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Consumer<ContainedNbtElement<?>>[] getListeners() {
        return this.dirtyListeners.toArray(Consumer[]::new);
    }

    @Override
    public String toString() {
        return this.containedArray.toString();
    }

    @Override
    public NbtLongArray copy() {
        return this.containedArray.copy();
    }

    @Override
    public boolean equals(Object object) {
        return this.containedArray.equals(object);
    }

    @Override
    public int hashCode() {
        return this.containedArray.hashCode();
    }

    @Override
    public void accept(NbtElementVisitor nbtElementVisitor) {
        this.containedArray.accept(nbtElementVisitor);
    }

    @Override
    public long[] getLongArray() {
        return this.containedArray.getLongArray();
    }

    @Override
    public int size() {
        return this.containedArray.size();
    }

    @Override
    public NbtLong get(int i) {
        return this.containedArray.get(i);
    }

    @Override
    public void add(int i, NbtLong nbtLong) {
        this.containedArray.add(i, nbtLong);
        this.dirty();
    }

    @Override
    public boolean setElement(int i, NbtElement nbtElement) {
        boolean result = this.containedArray.setElement(i, nbtElement);
        if (result) this.dirty();
        return result;
    }

    @Override
    public boolean addElement(int i, NbtElement nbtElement) {
        boolean result = this.containedArray.addElement(i, nbtElement);
        if (result) this.dirty();
        return result;
    }

    @Override
    public NbtLong remove(int i) {
        NbtLong result = this.containedArray.remove(i);
        this.dirty();
        return result;
    }

    @Override
    public boolean add(NbtLong nbtLong) {
        boolean result = this.containedArray.add(nbtLong);
        if (result) this.dirty();
        return result;
    }

    @Override
    public void clear() {
        this.containedArray.clear();
        this.dirty();
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner nbtScanner) {
        NbtScanner.Result result = this.containedArray.doAccept(nbtScanner);
        this.dirty();
        return result;
    }

    @Override
    public NbtLong set(int i, NbtLong nbtElement) {
        NbtLong result = this.containedArray.set(i, nbtElement);
        this.dirty();
        return result;
    }
}
