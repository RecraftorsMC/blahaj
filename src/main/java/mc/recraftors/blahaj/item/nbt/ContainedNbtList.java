package mc.recraftors.blahaj.item.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.scanner.NbtScanner;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ContainedNbtList extends NbtList implements ContainedNbtElement<NbtList> {
    @NotNull private final NbtList containedList;
    @NotNull private final Set<Consumer<ContainedNbtElement<?>>> dirtyListeners;
    private boolean modified;
    private boolean busy;

    @SuppressWarnings("UseBulkOperation")
    public ContainedNbtList(NbtList containedNbtList, Consumer<ContainedNbtElement<?>>... listeners) {
        super();
        this.containedList = containedNbtList;
        this.dirtyListeners = new HashSet<>();
        this.modified = false;
        this.busy = false;
        Arrays.stream(listeners).forEach(this.dirtyListeners::add);
    }

    @Override
    public final void dirty() {
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
    public NbtList getContained() {
        return this.containedList;
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
        return this.containedList.toString();
    }

    @Override
    public NbtElement remove(int i) {
        NbtElement result = this.containedList.remove(i);
        this.dirty();
        return result;
    }

    @Override
    public boolean isEmpty() {
        return this.containedList.isEmpty();
    }

    @Override
    public NbtCompound getCompound(int i) {
        return new ContainedNbtCompound(this.containedList.getCompound(i), this.getListeners());
    }

    @Override
    public NbtList getList(int i) {
        return new ContainedNbtList(this.containedList.getList(i), this.getListeners());
    }

    @Override
    public short getShort(int i) {
        return this.containedList.getShort(i);
    }

    @Override
    public int getInt(int i) {
        return this.containedList.getInt(i);
    }

    @Override
    public int[] getIntArray(int i) {
        return this.containedList.getIntArray(i);
    }

    @Override
    public long[] getLongArray(int i) {
        return this.containedList.getLongArray(i);
    }

    @Override
    public double getDouble(int i) {
        return this.containedList.getDouble(i);
    }

    @Override
    public float getFloat(int i) {
        return this.containedList.getFloat(i);
    }

    @Override
    public String getString(int i) {
        return this.containedList.getString(i);
    }

    @Override
    public int size() {
        return this.containedList.size();
    }

    @Override
    public NbtElement get(int i) {
        return ContainedNbtElement.getContained(this.containedList.get(i), this.getListeners());
    }

    @Override
    public NbtElement set(int i, NbtElement nbtElement) {
        NbtElement result = this.containedList.set(i, nbtElement);
        this.dirty();
        return result;
    }

    @Override
    public void add(int i, NbtElement nbtElement) {
        this.containedList.add(i, nbtElement);
        this.dirty();
    }

    @Override
    public boolean setElement(int i, NbtElement nbtElement) {
        boolean result = this.containedList.setElement(i, nbtElement);
        if (result) this.dirty();
        return result;
    }

    @Override
    public boolean addElement(int i, NbtElement nbtElement) {
        boolean result = this.containedList.addElement(i, nbtElement);
        if (result) this.dirty();
        return result;
    }

    @Override
    public NbtList copy() {
        //TODO watch for potential need to make it contained
        return this.containedList.copy();
    }

    @Override
    public boolean equals(Object object) {
        return this.containedList.equals(object);
    }

    @Override
    public int hashCode() {
        return this.containedList.hashCode();
    }

    @Override
    public void accept(NbtScanner nbtScanner) {
        this.containedList.accept(nbtScanner);
        this.dirty();
    }

    @Override
    public void clear() {
        this.containedList.clear();
        this.dirty();
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner nbtScanner) {
        NbtScanner.Result result = this.containedList.doAccept(nbtScanner);
        this.dirty();
        return result;
    }
}
