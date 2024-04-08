package mc.recraftors.blahaj.item.nbt;

import net.minecraft.nbt.*;

import java.util.function.Consumer;

public interface ContainedNbtElement <T extends NbtElement> {
    void clean();
    void dirty();
    T getContained();
    boolean isDirty();
    Consumer<ContainedNbtElement<?>>[] getListeners();

    @SuppressWarnings("unchecked")
    static <U extends NbtElement> U getContained(U element, Consumer<ContainedNbtElement<?>>... listeners) {
        if (element instanceof ContainedNbtElement<?> contained) {
            return (U) contained;
        }
        if (element instanceof NbtByteArray byteArray) {
            return (U) new ContainedNbtByteArray(byteArray, listeners);
        }
        if (element instanceof NbtCompound compound) {
            return (U) new ContainedNbtCompound(compound, listeners);
        }
        if (element instanceof NbtIntArray intArray) {
            return (U) new ContainedNbtIntArray(intArray, listeners);
        }
        if (element instanceof NbtList list) {
            return (U) new ContainedNbtList(list, listeners);
        }
        if (element instanceof NbtLongArray longArray) {
            return (U) new ContainedNbtLongArray(longArray, listeners);
        }
        return element;
    }
}
