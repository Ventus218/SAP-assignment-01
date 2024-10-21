package sap.ass01.solution.backend.hexagonal.ports.persistence;

import java.util.*;
import sap.ass01.solution.backend.hexagonal.ports.persistence.exceptions.*;
import sap.ass01.solution.backend.hexagonal.domain.model.EntityId;

public interface Repository<T> {

    public Collection<T> getAll();

    public void insert(EntityId<T> id, T entity) throws DuplicateIdException;

    public void update(EntityId<T> id, T entity) throws NotInRepositoryException;

    public Optional<T> find(EntityId<T> id);

    public void delete(EntityId<T> id) throws NotInRepositoryException;
}
