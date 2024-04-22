package mc.recraftors.blahaj.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.List;
import java.util.Optional;

public final class PreLaunchUtilsImpl {

    public static boolean isModLoaded(String modId) {
        List<ModInfo> mods = LoadingModList.get().getMods();
        for (ModInfo mod : mods) {
            if (mod.getModId().equals(modId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean modHasAuthor(String modId, String author) {
        Optional<?> opt = ModList.get().getModFileById(modId).getMods().get(0).getConfig().getConfigElement("authors");
        if (opt.isEmpty()) return false;
        String[] strings;
        Object o = opt.get();
        if (o instanceof String s) {
            strings = s.split("(,|\\s(-+)\\s)");
        } else if (o instanceof String[] array) {
            strings = array;
        } else {
            return false;
        }
        for (String s : strings) {
            s = s.trim().toLowerCase();
            if (s.replace(' ', '_').replace('-', '_').equals(author)) {
                return true;
            }
        }
        return false;
    }
}