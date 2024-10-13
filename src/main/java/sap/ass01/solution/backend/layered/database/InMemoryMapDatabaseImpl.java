package sap.ass01.solution.backend.layered.database;

import java.util.*;
import io.vertx.core.json.JsonObject;

public class InMemoryMapDatabaseImpl implements InMemoryMapDatabase {

    private final Map<String, Map<String, JsonObject>> maps = new HashMap<>();

    @Override
    public Map<String, JsonObject> createMap(String mapName) {
        if (maps.containsKey(mapName)) {
            throw new IllegalStateException("A map with name " + mapName + " already exists.");
        }
        Map<String, JsonObject> map = new HashMap<>();
        maps.put(mapName, map);
        return map;
    }

    @Override
    public void deleteMap(String mapName) {
        if (!maps.containsKey(mapName)) {
            throw new IllegalStateException("A map with name " + mapName + " does not exist.");
        }
        maps.remove(mapName);
    }

    @Override
    public Map<String, JsonObject> getMap(String mapName) {
        if (!maps.containsKey(mapName)) {
            throw new IllegalStateException("A map with name " + mapName + " does not exist.");
        }
        return maps.get(mapName);
    }

}
