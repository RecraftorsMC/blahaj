package mc.recraftors.blahaj.mixin.compat;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class Plugin implements IMixinConfigPlugin {
    private static final String COMPAT_PACKAGE_ROOT;
    private static final int COMPAT_PACKAGE_LENGTH;
    private static final String COMPAT_PRESENT_KEY = "present";
    private static final String COMPAT_ABSENT_KEY = "absent";
    private static final String COMPAT_ANY_KEY = "any";

    static {
        // Shorthand getting the plugin package to ensure not making trouble with other mixins
        COMPAT_PACKAGE_ROOT = Plugin.class.getPackageName();
        String[] compatRoot = COMPAT_PACKAGE_ROOT.split("\\.");
        COMPAT_PACKAGE_LENGTH = compatRoot.length;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(COMPAT_PACKAGE_ROOT)) {
            return true;
        }
        String[] mixinPath = mixinClassName.split("\\.");
        // the id of the target mod
        String compatModId = mixinPath[COMPAT_PACKAGE_LENGTH];
        // Apply accordingly of the mod's presence, absence, etc
        if (mixinPath[COMPAT_PACKAGE_LENGTH+1].equals(COMPAT_PRESENT_KEY)) return FabricLoader.getInstance().isModLoaded(compatModId);
        else if (mixinPath[COMPAT_PACKAGE_LENGTH+1].equals(COMPAT_ABSENT_KEY)) return !FabricLoader.getInstance().isModLoaded(compatModId);
        else return (mixinPath[COMPAT_PACKAGE_LENGTH+1].equals(COMPAT_ANY_KEY));
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
