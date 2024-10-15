package sap.ass01.solution.backend.layered.persistence;

import java.util.*;

public interface CollectionStorage {

    public void createCollection(String collectionName);

    public void deleteCollection(String collectionName);

    public <T> Collection<T> getAllFromCollection(String collectionName, Class<T> type);

    public <T> void insert(String collectionName, String objectId, T object) throws DuplicateIdException;

    public <T> void update(String collectionName, String objectId, T object);

    public <T> T find(String collectionName, String objectId, Class<T> type);

    public void delete(String collectionName, String objectId);

}
