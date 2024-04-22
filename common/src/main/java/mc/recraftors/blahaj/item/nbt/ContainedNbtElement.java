package mc.recraftors.blahaj.item.nbt;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

public interface ContainedNbtElement <T extends NbtElement> {
    void clean();
    void dirty();
    T getContained();
    boolean isDirty();
    Consumer<ContainedNbtElement<?>>[] getListeners();

    @Contract
    @ExpectPlatform
    @SuppressWarnings("unchecked")
    static <U extends NbtElement> U getContained(U element, Consumer<ContainedNbtElement<?>>... listeners) {
        throw new UnsupportedOperationException();
    }
}
