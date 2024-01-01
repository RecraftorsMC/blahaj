package hibi.blahaj.mixin;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements Consumer<List<LootPool>> {

    @Mutable
    @Shadow @Final public List<LootPool> pools;

    @Override
    public void accept(List<LootPool> lootPools) {
        this.pools = lootPools;
    }
}
