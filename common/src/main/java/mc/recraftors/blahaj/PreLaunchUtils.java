package mc.recraftors.blahaj;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Contract;

public final class PreLaunchUtils {
    private PreLaunchUtils() {}

    @Contract
    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static boolean modHasAuthor(String modId, String author) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static boolean isForge() {
        return false;
    }
}
