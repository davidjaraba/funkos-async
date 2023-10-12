package dev.services;

import dev.database.models.Funko;
import dev.exceptions.FunkoNoEncontrado;
import dev.repositories.FunkosAsyncRepo;
import dev.repositories.FunkosAsyncRepoImpl;
import dev.services.cache.FunkosCache;
import dev.services.cache.FunkosCacheImpl;
import dev.services.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FunkosServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(FunkosServiceImpl.class);

    private static FunkosServiceImpl instance;
    private final FunkosAsyncRepoImpl repository;
    private final FunkosCacheImpl cache;
    private final DatabaseManager databaseManager;


    private FunkosServiceImpl(FunkosAsyncRepoImpl repo, DatabaseManager databaseManager, FunkosCacheImpl cache) {

        this.cache = cache;
        this.repository = repo;
        this.databaseManager = databaseManager;

    }

    public static FunkosServiceImpl getInstance(FunkosAsyncRepoImpl repo, DatabaseManager databaseManager, FunkosCacheImpl cache){
        if (instance == null) {
            instance = new FunkosServiceImpl(repo, databaseManager, cache);
        }
        return instance;
    }

    public List<Funko> findAll() throws ExecutionException, InterruptedException {
        logger.info("Obteniendo todos los funkos");
        return repository.findAll().get();
    }

    public Optional<Funko> findByName(String name) throws ExecutionException, InterruptedException {
        logger.info("Obteniendo funko con nombre: "+name);
        return repository.findByName(name).get();
    }

    public Optional<Funko> findById(UUID id) throws ExecutionException, InterruptedException {
        logger.info("Obtenido funko con id "+id);
        Funko funko = cache.get(id);
        if (funko != null){
            return Optional.of(funko);
        }else{
            return repository.findById(id).get();
        }
    }

    public Funko save(Funko funko) throws ExecutionException, InterruptedException {
        logger.info("Guardando funko con id "+funko.codigo());
        repository.save(funko).get();
        cache.put(funko.codigo(), funko);
        return funko;
    }

    public Funko update(UUID id, Funko funko) throws ExecutionException, InterruptedException {
        logger.info("Actualizando funko con id "+funko.codigo());
        repository.update(id, funko).get();
        cache.put(funko.codigo(), funko);
        return funko;
    }

    public boolean delete(UUID id) throws ExecutionException, InterruptedException {
        logger.info("Eliminando funko con id "+id);
        boolean deleted = repository.delete(id).get();
        if (deleted){
            cache.remove(id);
        }
        return deleted;
    }


}
