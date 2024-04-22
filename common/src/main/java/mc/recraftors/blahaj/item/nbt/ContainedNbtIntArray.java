package mc.recraftors.blahaj.item.nbt;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ContainedNbtIntArray extends NbtIntArray implements ContainedNbtElement<NbtIntArray> {
    @NotNull private final NbtIntArray containedArray;
    @NotNull private final Set<Consumer<ContainedNbtElement<?>>> dirtyListeners;
    private boolean modified;
    private boolean busy;

    @SuppressWarnings("UseBulkOperation")
    public ContainedNbtIntArray(NbtIntArray nbtIntArray, Consumer<ContainedNbtElement<?>>... listeners) {
        super(new int[0]);
        this.containedArray = nbtIntArray;
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
    public NbtIntArray getContained() {
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
    public NbtIntArray copy() {
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
    public int[] getIntArray() {
        return this.containedArray.getIntArray();
    }

    @Override
    public int size() {
        return this.containedArray.size();
    }

    @Override
    public NbtInt get(int i) {
        return this.containedArray.get(i);
    }

    @Override
    public NbtInt set(int i, NbtInt nbtInt) {
        NbtInt result = this.containedArray.set(i, nbtInt);
        this.dirty();
        return result;
    }

    @Override
    public void add(int i, NbtInt nbtInt) {
        this.containedArray.add(i, nbtInt);
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
    public void clear() {
        this.containedArray.clear();
        this.dirty();
    }
}
