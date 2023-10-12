package dev.repositories;

import dev.database.models.Funko;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FunkosAsyncRepo extends CRUDRepo<Funko, UUID>{

    CompletableFuture<Optional<Funko>> findByName(String name);


}
