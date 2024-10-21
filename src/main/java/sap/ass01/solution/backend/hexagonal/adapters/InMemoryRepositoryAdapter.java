package sap.ass01.solution.backend.hexagonal.adapters;

import java.util.*;
import sap.ass01.solution.backend.hexagonal.domain.model.EntityId;
import sap.ass01.solution.backend.hexagonal.ports.persistence.Repository;
import sap.ass01.solution.backend.hexagonal.ports.persistence.exceptions.*;
import sap.ass01.solution.backend.hexagonal.technologies.persistence.InMemoryMapDatabase;

public class InMemoryRepositoryAdapter<T> implements Repository<T> {

    private final String entityName;
    private Map<String, T> map;

    @SuppressWarnings("unchecked")
    public InMemoryRepositoryAdapter(InMemoryMapDatabase db, String entityName) {
        this.entityName = entityName;
        try {
            map = (Map<String, T>) db.getMap(entityName);
        } catch (IllegalStateException e) {
            // Map doesn't exist
            map = (Map<String, T>) db.createMap(entityName);
        }
    }

    @Override
    public void delete(EntityId<T> id) throws NotInRepositoryException {
        if (!map.containsKey(id.id())) {
            throw new NotInRepositoryException("The " + entityName + " with id " + id.id() + " does not exist");
        }
        map.remove(id.id());
    }

    @Override
    public Optional<T> find(EntityId<T> id) {
        return Optional.ofNullable(map.get(id.id()));
    }

    @Override
    public Collection<T> getAll(Class<T> type) {
        return map.values().stream().toList();
    }

    @Override
    public void insert(EntityId<T> id, T entity) throws DuplicateIdException {
        if (map.containsKey(id.id())) {
            throw new DuplicateIdException("An " + entityName + " with id " + id.id() + " already exists");
        }
        map.put(id.id(), entity);
    }

    @Override
    public void update(EntityId<T> id, T entity) throws NotInRepositoryException {
        if (!map.containsKey(id.id())) {
            throw new NotInRepositoryException("The " + entityName + " with id " + id.id() + " does not exist");
        }
        map.put(id.id(), entity);
    }
}
