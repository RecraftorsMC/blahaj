package mc.recraftors.blahaj.mixin;

import com.google.common.collect.Lists;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements Consumer<LootPool[]>, Supplier<LootPool[]> {
    @Mutable
    @Shadow @Final private List<LootPool> pools;

    @Override
    public LootPool[] get() {
        return this.pools.toArray(LootPool[]::new);
    }

    @Override
    public void accept(LootPool[] lootPools) {
        this.pools = Lists.newArrayList(lootPools);
    }
}
