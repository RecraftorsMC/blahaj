package mc.recraftors.blahaj;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public final class ClientUtils {
    private ClientUtils() {}

    public static Optional<String> getPlayerName(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return Optional.empty();
        }
        if (client.player.getUuid().equals(uuid)) {
            return Optional.ofNullable(client.player.getDisplayName().getString());
        }
        Collection<PlayerListEntry> entries = client.player.networkHandler.getListedPlayerListEntries();
        return entries.stream().filter(e -> e.getProfile().getId().equals(uuid))
                .map(e -> e.getDisplayName() == null ? (String)null : e.getDisplayName().getString())
                .findFirst();
    }
}
