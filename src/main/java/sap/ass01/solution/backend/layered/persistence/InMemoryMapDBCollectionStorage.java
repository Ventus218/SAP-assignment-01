package sap.ass01.solution.backend.layered.persistence;

import java.util.Collection;
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
    public <T> Collection<T> getAllFromCollection(String collectionName, Class<T> type) {
        return db.getMap(collectionName).values().stream().map(type::cast).toList();
    }

    @Override
    public <T> void insert(String collectionName, String objectId, T jsonObject) {
        var map = db.getMap(collectionName);
        if (map.containsKey(objectId)) {
            throw new IllegalArgumentException("An object with id " + objectId + " already exists");
        }
        map.put(objectId, jsonObject);
    }

    @Override
    public <T> void update(String collectionName, String objectId, T jsonObject) {
        var map = db.getMap(collectionName);
        if (!map.containsKey(objectId)) {
            throw new IllegalArgumentException("The object with id " + objectId + " does not exist");
        }
        map.put(objectId, jsonObject);
    }

    @Override
    public <T> T get(String collectionName, String objectId, Class<T> type) {
        var map = db.getMap(collectionName);
        if (!map.containsKey(objectId)) {
            throw new IllegalArgumentException("The object with id " + objectId + " does not exist");
        }
        return type.cast(map.get(objectId));
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
