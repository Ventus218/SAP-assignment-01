package sap.ass01.solution.backend.layered.persistence;

import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;
import io.vertx.core.json.*;
import sap.ass01.solution.backend.layered.database.FileSystemDatabase;

class FileSystemDBCollectionStorage implements CollectionStorage {

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
        return collectionName + ".txt";
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
    public JsonArray getAllFromCollection(String collectionName) {
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

    @Override
    public void insert(String collectionName, String objectId, JsonObject jsonObject) {
        if (getAllFromCollection(collectionName).stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(objectId))) {
            throw new IllegalStateException("An object with id " + objectId + " already exists.");
        }

        JsonObject obj = new JsonObject().put("id", objectId).put("object", jsonObject);
        JsonArray newJsonArray = getAllFromCollection(collectionName).add(obj);
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
    public void update(String collectionName, String objectId, JsonObject jsonObject) {
        if (!getAllFromCollection(collectionName).stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(objectId))) {
            throw new IllegalStateException("An object with id " + objectId + " does not exist.");
        }

        JsonObject obj = new JsonObject().put("id", objectId).put("object", jsonObject);
        var listWithoutOldObject = getAllFromCollection(collectionName).stream()
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
    public JsonObject get(String collectionName, String objectId) {
        var optional = getAllFromCollection(collectionName).stream()
                .filter(o -> ((JsonObject) o).getString("id").equals(objectId))
                .findFirst();
        if (optional.isEmpty()) {
            throw new IllegalStateException("An object with id " + objectId + " does not exist.");
        }
        return ((JsonObject) optional.get()).getJsonObject("object");
    }

    @Override
    public void delete(String collectionName, String objectId) {
        if (!getAllFromCollection(collectionName).stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(objectId))) {
            throw new IllegalStateException("An object with id " + objectId + " does not exist.");
        }

        var listWithoutOldObject = getAllFromCollection(collectionName).stream()
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

}
