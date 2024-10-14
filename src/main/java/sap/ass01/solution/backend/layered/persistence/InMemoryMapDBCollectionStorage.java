package sap.ass01.solution.backend.layered.persistence;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.ass01.solution.backend.layered.database.InMemoryMapDatabase;

class InMemoryMapDBCollectionStorage implements CollectionStorage {

    private final InMemoryMapDatabase db;

    public InMemoryMapDBCollectionStorage(InMemoryMapDatabase db) {
        this.db = db;
    }

    @Override
    public void createCollection(String collectionName) {
        db.createMap(collectionName);
    }

    @Override
    public void deleteCollection(String collectionName) {
        db.deleteMap(collectionName);
    }

    @Override
    public JsonArray getAllFromCollection(String collectionName) {
        return new JsonArray(db.getMap(collectionName).values().stream().toList());
    }

    @Override
    public void insert(String collectionName, String objectId, JsonObject jsonObject) {
        var map = db.getMap(collectionName);
        if (map.containsKey(objectId)) {
            throw new IllegalArgumentException("An object with id " + objectId + " already exists");
        }
        map.put(objectId, jsonObject);
    }

    @Override
    public void update(String collectionName, String objectId, JsonObject jsonObject) {
        var map = db.getMap(collectionName);
        if (!map.containsKey(objectId)) {
            throw new IllegalArgumentException("The object with id " + objectId + " does not exist");
        }
        map.put(objectId, jsonObject);
    }

    @Override
    public JsonObject get(String collectionName, String objectId) {
        var map = db.getMap(collectionName);
        if (!map.containsKey(objectId)) {
            throw new IllegalArgumentException("The object with id " + objectId + " does not exist");
        }
        return map.get(objectId);
    }

    @Override
    public void delete(String collectionName, String objectId) {
        var map = db.getMap(collectionName);
        if (!map.containsKey(objectId)) {
            throw new IllegalArgumentException("The object with id " + objectId + " does not exist");
        }
        map.remove(objectId);
    }

}
