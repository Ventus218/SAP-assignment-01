package sap.ass01.solution.backend.layered.database;

import java.util.*;
import io.vertx.core.json.JsonObject;

public interface InMemoryMapDatabase {

    /**
     * @throws IllegalStateException if a map with the given name already exists
     * @param mapName
     * @return the new map
     */
    public Map<String, JsonObject> createMap(String mapName);

    /**
     * @throws IllegalStateException if a map with the given name does not exist
     * @param mapName
     */
    public void deleteMap(String mapName);

    /**
     * @throws IllegalStateException if a map with the given name does not exist
     * @param mapName
     * @return the searched map
     */
    public Map<String, JsonObject> getMap(String mapName);

}
