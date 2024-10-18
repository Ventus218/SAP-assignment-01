package sap.ass01.solution.backend.layered.persistence;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.*;
import io.vertx.core.json.*;
import sap.ass01.solution.backend.layered.database.FileSystemDatabase;
import sap.ass01.solution.backend.layered.persistence.exceptions.DuplicateIdException;
import sap.ass01.solution.backend.layered.persistence.exceptions.ItemNotPersistedException;

public class FileSystemDBCollectionStorage implements CollectionStorage {

    /*
     * --- ABOUT FILE FORMAT ---
     * 
     * Every file contains a serialized JsonArray of JsonObject where each
     * JsonObject has two fields:
     * 
     * - id: the unique id of the object
     * - object: the actual object that the user is persisting
     */

    private final FileSystemDatabase db;

    public FileSystemDBCollectionStorage(FileSystemDatabase db) {
        this.db = db;
    }

    private String collectionFileName(String collectionName) {
        return collectionName + ".json";
    }

    @Override
    public void createCollection(String collectionName) {
        try {
            File file = db.createFile(collectionFileName(collectionName));
            Files.write(file.toPath(), new JsonArray().encode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong while creating file " + collectionFileName(collectionName), e);
        }
    }

    @Override
    public void deleteCollection(String collectionName) {
        db.deleteFile(collectionFileName(collectionName));
    }

    @Override
    public <T> Collection<T> getAllFromCollection(String collectionName, Class<T> type) {
        var jsonArray = getJsonArrayFromCollection(collectionName);
        return jsonArray.stream().map(o -> ((JsonObject) o).getJsonObject("object"))
                .map(o -> o.mapTo(type)).toList();
    }

    @Override
    public <T> void insert(String collectionName, String objectId, T jsonObject) throws DuplicateIdException {
        if (getJsonArrayFromCollection(collectionName).stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(objectId))) {
            throw new DuplicateIdException("An object with id " + objectId + " already exists.");
        }

        JsonObject obj = new JsonObject().put("id", objectId).put("object", jsonObject);
        JsonArray newJsonArray = getJsonArrayFromCollection(collectionName).add(obj);
        File file = db.getFile(collectionFileName(collectionName));
        try {
            Files.write(file.toPath(), newJsonArray.encode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong while inserting object with id " + objectId + " into file "
                            + collectionFileName(collectionName),
                    e);
        }

    }

    @Override
    public <T> void update(String collectionName, String objectId, T jsonObject) throws ItemNotPersistedException {
        if (!getJsonArrayFromCollection(collectionName).stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(objectId))) {
            throw new ItemNotPersistedException("An object with id " + objectId + " does not exist.");
        }

        JsonObject obj = new JsonObject().put("id", objectId).put("object", jsonObject);
        var listWithoutOldObject = getJsonArrayFromCollection(collectionName).stream()
                .filter(o -> !((JsonObject) o).getString("id").equals(objectId)).collect(Collectors.toList());
        var arrayWithoutOldObject = new JsonArray(listWithoutOldObject);
        var newJsonArray = arrayWithoutOldObject.add(obj);
        File file = db.getFile(collectionFileName(collectionName));
        try {
            Files.write(file.toPath(), newJsonArray.encode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong while updating object with id " + objectId + " into file "
                            + collectionFileName(collectionName),
                    e);
        }
    }

    @Override
    public <T> Optional<T> find(String collectionName, String objectId, Class<T> type) {
        var optional = getJsonArrayFromCollection(collectionName).stream()
                .filter(o -> ((JsonObject) o).getString("id").equals(objectId))
                .findFirst();
        return optional.map(o -> ((JsonObject) o)).map(o -> o.getJsonObject("object").mapTo(type));
    }

    @Override
    public void delete(String collectionName, String objectId) throws ItemNotPersistedException {
        if (!getJsonArrayFromCollection(collectionName).stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(objectId))) {
            throw new ItemNotPersistedException("An object with id " + objectId + " does not exist.");
        }

        var listWithoutOldObject = getJsonArrayFromCollection(collectionName).stream()
                .filter(o -> !((JsonObject) o).getString("id").equals(objectId)).collect(Collectors.toList());
        var newJsonArray = new JsonArray(listWithoutOldObject);
        File file = db.getFile(collectionFileName(collectionName));
        try {
            Files.write(file.toPath(), newJsonArray.encode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong while deleting object with id " + objectId + " into file "
                            + collectionFileName(collectionName),
                    e);
        }
    }

    private JsonArray getJsonArrayFromCollection(String collectionName) {
        var file = db.getFile(collectionFileName(collectionName));
        String fileContent;
        try {
            fileContent = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong while reading file " + collectionFileName(collectionName),
                    e);
        }
        return new JsonArray(fileContent);
    }
}
