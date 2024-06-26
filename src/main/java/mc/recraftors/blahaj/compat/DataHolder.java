package mc.recraftors.blahaj.compat;

import com.google.gson.internal.LinkedTreeMap;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DataHolder {
    private DataHolder() {}

    public static final Map<String, Map<String, Object>> compatDataMap = new LinkedTreeMap<>();

    public static Map<String, Object> modMap(String modId) {
        Map<String, Object> modMap = DataHolder.compatDataMap.putIfAbsent(modId, new LinkedHashMap<>());
        if (modMap == null) {
            modMap = new LinkedHashMap<>();
            DataHolder.compatDataMap.put(modId, modMap);
        }
        return modMap;
    }
}
