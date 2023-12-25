package hibi.blahaj.mixin;

import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(LootDataKey.class)
public abstract class LootDataKeyMixin <T> {
    @Shadow @Final private Identifier id;

    @Shadow @Final private LootDataType<T> comp_1474;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LootDataKey<?> key)) return false;
        return this.id.equals(key.id()) && this.comp_1474.equals(key.comp_1474());
    }
}
