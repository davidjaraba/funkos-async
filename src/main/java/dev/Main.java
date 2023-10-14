package dev;

import dev.controllers.FunkosControllerImpl;
import dev.database.models.Funko;
import dev.repositories.FunkosAsyncRepoImpl;
import dev.services.FunkosServiceImpl;
import dev.services.cache.FunkosCacheImpl;
import dev.services.database.DatabaseManager;
import dev.services.generator.IdGenerator;
import dev.services.storage.FunkosStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(FunkosServiceImpl.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        IdGenerator idGenerator = new IdGenerator();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        FunkosAsyncRepoImpl funkosAsyncRepoImpl = FunkosAsyncRepoImpl.getInstance(idGenerator, databaseManager);
        FunkosStorageImpl storage = new FunkosStorageImpl(funkosAsyncRepoImpl);
        FunkosCacheImpl funkosCache = new FunkosCacheImpl();
        FunkosServiceImpl funkosService = FunkosServiceImpl.getInstance(funkosAsyncRepoImpl, databaseManager, funkosCache, storage);
        FunkosControllerImpl funkosController = new FunkosControllerImpl(funkosService);


        storage.importCSVToDB();

        funkosService.backup();


        logger.info("Funko mas caro");
        logger.info(funkosController.mostExpensiveFunko().toString());

        logger.info("Media de precio de funkos");
        logger.info(Double.toString(funkosController.averagePrice()));

        logger.info("Funkos por modelo");
        funkosController.funkosGroupedByModel().forEach((k, v) -> logger.info("Modelo "+k + " " + v));

        logger.info("Numero de funkos por modelo");
        funkosController.numFunkosGroupedByModel().forEach((k, v) -> logger.info("Modelo "+k + " " + v));

        logger.info("Funkos lanzados en 2023");
        funkosController.funkosReleasedInYear(2023).forEach(k -> logger.info(k.toString()));

        logger.info("Numero de funkos de Stitch: "+funkosController.numFunkosContainWord("stitch"));

        logger.info("Funkos de Stitch");
        funkosController.funkosContainWord("stitch").forEach(k -> logger.info(k.toString()));




        funkosCache.shutdown();

    }
}