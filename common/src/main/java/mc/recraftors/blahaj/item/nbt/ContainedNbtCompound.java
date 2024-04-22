package mc.recraftors.blahaj.item.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.scanner.NbtScanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ContainedNbtCompound extends NbtCompound implements ContainedNbtElement<NbtCompound> {
    @NotNull private final NbtCompound containedCompound;
    @NotNull private final Set<Consumer<ContainedNbtElement<?>>> dirtyListeners;
    private boolean modified;
    private boolean busy;

    @SuppressWarnings("UseBulkOperation") // lmfao shut up with "Arrays.asList", it is literally useless computation
    public ContainedNbtCompound(@NotNull NbtCompound containedNbtCompound, Consumer<ContainedNbtElement<?>>... listeners) {
        super();
        this.containedCompound = containedNbtCompound;
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
    public NbtCompound getContained() {
        return this.containedCompound;
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
    public Set<String> getKeys() {
        return this.containedCompound.getKeys();
    }

    @Override
    public int getSize() {
        return this.containedCompound.getSize();
    }

    @Nullable
    @Override
    public NbtElement put(String string, NbtElement nbtElement) {
        NbtElement result = this.containedCompound.put(string, nbtElement);
        this.dirty();
        return result;
    }

    @Override
    public void putByte(String string, byte b) {
        this.containedCompound.putByte(string, b);
        this.dirty();
    }

    @Override
    public void putShort(String string, short s) {
        this.containedCompound.putShort(string, s);
        this.dirty();
    }

    @Override
    public void putInt(String string, int i) {
        this.containedCompound.putInt(string, i);
        this.dirty();
    }

    @Override
    public void putLong(String string, long l) {
        this.containedCompound.putLong(string, l);
        this.dirty();
    }

    @Override
    public void putUuid(String string, UUID uUID) {
        this.containedCompound.putUuid(string, uUID);
        this.dirty();
    }

    @Override
    public UUID getUuid(String string) {
        return this.containedCompound.getUuid(string);
    }

    @Override
    public boolean containsUuid(String string) {
        return this.containedCompound.containsUuid(string);
    }

    @Override
    public void putFloat(String string, float f) {
        this.containedCompound.putFloat(string, f);
        this.dirty();
    }

    @Override
    public void putDouble(String string, double d) {
        this.containedCompound.putDouble(string, d);
        this.dirty();
    }

    @Override
    public void putString(String string, String string2) {
        this.containedCompound.putString(string, string2);
        this.dirty();
    }

    @Override
    public void putByteArray(String string, byte[] bs) {
        this.containedCompound.putByteArray(string, bs);
        this.dirty();
    }

    @Override
    public void putByteArray(String string, List<Byte> list) {
        this.containedCompound.putByteArray(string, list);
        this.dirty();
    }

    @Override
    public void putIntArray(String string, int[] is) {
        this.containedCompound.putIntArray(string, is);
        this.dirty();
    }

    @Override
    public void putIntArray(String string, List<Integer> list) {
        this.containedCompound.putIntArray(string, list);
        this.dirty();
    }

    @Override
    public void putLongArray(String string, long[] ls) {
        this.containedCompound.putLongArray(string, ls);
        this.dirty();
    }

    @Override
    public void putLongArray(String string, List<Long> list) {
        this.containedCompound.putLongArray(string, list);
        this.dirty();
    }

    @Override
    public void putBoolean(String string, boolean bl) {
        this.containedCompound.putBoolean(string, bl);
        this.dirty();
    }

    @Nullable
    @Override
    public NbtElement get(String string) {
        return ContainedNbtElement.getContained(this.containedCompound.get(string), this.getListeners());
    }

    @Override
    public byte getType(String string) {
        return this.containedCompound.getType(string);
    }

    @Override
    public boolean contains(String string) {
        return this.containedCompound.contains(string);
    }

    @Override
    public boolean contains(String string, int i) {
        return this.containedCompound.contains(string, i);
    }

    @Override
    public byte getByte(String string) {
        return this.containedCompound.getByte(string);
    }

    @Override
    public short getShort(String string) {
        return this.containedCompound.getShort(string);
    }

    @Override
    public int getInt(String string) {
        return this.containedCompound.getInt(string);
    }

    @Override
    public long getLong(String string) {
        return this.containedCompound.getLong(string);
    }

    @Override
    public float getFloat(String string) {
        return this.containedCompound.getFloat(string);
    }

    @Override
    public double getDouble(String string) {
        return this.containedCompound.getDouble(string);
    }

    @Override
    public String getString(String string) {
        return this.containedCompound.getString(string);
    }

    @Override
    public byte[] getByteArray(String string) {
        return this.containedCompound.getByteArray(string);
    }

    @Override
    public int[] getIntArray(String string) {
        return this.containedCompound.getIntArray(string);
    }

    @Override
    public long[] getLongArray(String string) {
        return this.containedCompound.getLongArray(string);
    }

    @Override
    public NbtCompound getCompound(String string) {
        return this.containedCompound.getCompound(string);
    }

    @Override
    public NbtList getList(String string, int i) {
        return new ContainedNbtList(this.containedCompound.getList(string, i), this.getListeners());
    }

    @Override
    public boolean getBoolean(String string) {
        return this.containedCompound.getBoolean(string);
    }

    @Override
    public void remove(String string) {
        this.containedCompound.remove(string);
        this.dirty();
    }

    @Override
    public String toString() {
        return this.containedCompound.toString();
    }

    @Override
    public boolean isEmpty() {
        return this.containedCompound.isEmpty();
    }

    @Override
    public NbtCompound copy() {
        return this.containedCompound.copy();
    }

    @Override
    public boolean equals(Object object) {
        return this.containedCompound.equals(object);
    }

    @Override
    public int hashCode() {
        return this.containedCompound.hashCode();
    }

    @Override
    public NbtCompound copyFrom(NbtCompound nbtCompound) {
        this.containedCompound.copyFrom(nbtCompound);
        this.dirty();
        return this;
    }

    @Override
    public void accept(NbtScanner nbtScanner) {
        this.containedCompound.accept(nbtScanner);
        this.dirty();
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner nbtScanner) {
        return this.containedCompound.doAccept(nbtScanner);
    }
}
