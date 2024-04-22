package mc.recraftors.blahaj.item.nbt;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ContainedNbtByteArray extends NbtByteArray implements ContainedNbtElement<NbtByteArray> {
    @NotNull private final NbtByteArray containedArray;
    @NotNull private final Set<Consumer<ContainedNbtElement<?>>> dirtyListeners;
    private boolean modified;
    private boolean busy;

    @SuppressWarnings("UseBulkOperation")
    public ContainedNbtByteArray(NbtByteArray nbtByteArray, Consumer<ContainedNbtElement<?>>... listeners) {
        super(new byte[0]);
        this.containedArray = nbtByteArray;
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
    public NbtByteArray getContained() {
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
    public NbtElement copy() {
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
    public byte[] getByteArray() {
        return this.containedArray.getByteArray();
    }

    @Override
    public int size() {
        return this.containedArray.size();
    }

    @Override
    public NbtByte get(int i) {
        return this.containedArray.get(i);
    }

    @Override
    public NbtByte set(int i, NbtByte nbtByte) {
        NbtByte result = this.containedArray.set(i, nbtByte);
        this.dirty();
        return result;
    }

    @Override
    public void method_10531(int i, NbtByte nbtByte) {
        this.containedArray.method_10531(i, nbtByte);
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
    public NbtByte method_10536(int i) {
        NbtByte result = this.containedArray.method_10536(i);
        this.dirty();
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
    public void add(int i, NbtByte nbtElement) {
        this.containedArray.add(i, nbtElement);
        this.dirty();
    }

    @Override
    public NbtByte remove(int i) {
        NbtByte result = this.containedArray.remove(i);
        this.dirty();
        return result;
    }
}
