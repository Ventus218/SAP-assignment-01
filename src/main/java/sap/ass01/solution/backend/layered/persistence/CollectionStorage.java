package sap.ass01.solution.backend.layered.persistence;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface CollectionStorage {

    public void createCollection(String collectionName);

    public void deleteCollection(String collectionName);

    public JsonArray getAllFromCollection(String collectionName);

    public void insert(String collectionName, String objectId, JsonObject jsonObject);

    public void update(String collectionName, String objectId, JsonObject jsonObject);

    public JsonObject get(String collectionName, String objectId);

    public void delete(String collectionName, String objectId);

}
