package sap.ass01.solution.backend.hexagonal.adapters.persistence;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import io.vertx.core.json.*;
import sap.ass01.solution.backend.hexagonal.domain.model.EntityId;
import sap.ass01.solution.backend.hexagonal.ports.persistence.exceptions.*;
import sap.ass01.solution.backend.hexagonal.technologies.persistence.FileSystemDatabase;
import sap.ass01.solution.backend.hexagonal.ports.persistence.Repository;

public class FileSystemRepositoryAdapter<T> implements Repository<T> {

    private final String entityName;
    private final Class<T> entityType;
    private File file;

    public FileSystemRepositoryAdapter(FileSystemDatabase db, String entityName, Class<T> entityType) {
        this.entityName = entityName;
        this.entityType = entityType;
        String filename = entityName + ".json";
        try {
            file = db.getFile(filename);
        } catch (Exception e) {
            // File doesn't exist
            try {
                file = db.createFile(filename);
                Files.write(file.toPath(), new JsonArray().encode().getBytes());
            } catch (IOException e1) {
                throw new RuntimeException("Something went wrong while creating file " + filename, e);
            }
        }
    }

    @Override
    public void delete(EntityId<T> id) throws NotInRepositoryException {
        if (!getJsonArray().stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(id.id()))) {
            throw new NotInRepositoryException("An " + entityName + " with id " + id.id() + " does not exist.");
        }

        var listWithoutOldObject = getJsonArray().stream()
                .filter(o -> !((JsonObject) o).getString("id").equals(id.id())).collect(Collectors.toList());
        var newJsonArray = new JsonArray(listWithoutOldObject);
        try {
            Files.write(file.toPath(), newJsonArray.encode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong while deleting " + entityName + " with id " + id.id() + " into file "
                            + file,
                    e);
        }
    }

    @Override
    public Optional<T> find(EntityId<T> id) {
        var optional = getJsonArray().stream()
                .filter(o -> ((JsonObject) o).getString("id").equals(id.id()))
                .findFirst();
        return optional.map(o -> ((JsonObject) o)).map(o -> o.getJsonObject("object").mapTo(id.type()));
    }

    @Override
    public Collection<T> getAll() {
        var jsonArray = getJsonArray();
        return jsonArray.stream().map(o -> ((JsonObject) o).getJsonObject("object"))
                .map(o -> o.mapTo(entityType)).toList();
    }

    @Override
    public void insert(EntityId<T> id, T entity) throws DuplicateIdException {
        if (getJsonArray().stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(id.id()))) {
            throw new DuplicateIdException("An " + entityName + " with id " + id.id() + " already exists.");
        }

        JsonObject obj = new JsonObject().put("id", id.id()).put("object", JsonObject.mapFrom(entity));
        JsonArray newJsonArray = getJsonArray().add(obj);
        try {
            Files.write(file.toPath(), newJsonArray.encode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong while inserting an " + entityName + " with id " + id.id() + " into file "
                            + file,
                    e);
        }

    }

    @Override
    public void update(EntityId<T> id, T entity) throws NotInRepositoryException {
        if (!getJsonArray().stream()
                .anyMatch(o -> ((JsonObject) o).getString("id").equals(id.id()))) {
            throw new NotInRepositoryException("An " + entityName + " with id " + id.id() + " does not exist.");
        }

        JsonObject obj = new JsonObject().put("id", id.id()).put("object", JsonObject.mapFrom(entity));
        var listWithoutOldObject = getJsonArray().stream()
                .filter(o -> !((JsonObject) o).getString("id").equals(id.id())).collect(Collectors.toList());
        var arrayWithoutOldObject = new JsonArray(listWithoutOldObject);
        var newJsonArray = arrayWithoutOldObject.add(obj);
        try {
            Files.write(file.toPath(), newJsonArray.encode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong while updating " + entityName + " with id " + id.id() + " into file "
                            + file,
                    e);
        }
    }

    private JsonArray getJsonArray() {
        try {
            return new JsonArray(new String(Files.readAllBytes(file.toPath())));
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong while reading file " + file, e);
        }
    }

}
