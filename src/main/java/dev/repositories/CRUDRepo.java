package dev.repositories;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CRUDRepo<T, ID> {

    CompletableFuture<List<T>> findAll() throws SQLException, IOException;

    CompletableFuture<Optional<T>> findById(ID id) throws SQLException, IOException;

    CompletableFuture<T> save(T entity) throws SQLException, IOException;

    CompletableFuture<T> update(ID id, T entity) throws SQLException, IOException;

    CompletableFuture<Boolean> delete(ID id) throws SQLException, IOException;

}