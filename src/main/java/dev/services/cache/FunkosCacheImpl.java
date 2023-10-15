package dev.services.cache;

import dev.database.models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class FunkosCacheImpl implements FunkosCache<UUID, Funko> {

    private final Logger logger = LoggerFactory.getLogger(FunkosCacheImpl.class);
    private final int CACHE_SIZE = 10;
    private final Map<UUID, Funko> cache;
    private final ScheduledExecutorService executorService;


    public FunkosCacheImpl(){
        this.cache = Collections.synchronizedMap(new LinkedHashMap<>(CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<UUID, Funko> eldest) {
                return size() > CACHE_SIZE;
            }
        });

        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(this::clear, 2, 2, TimeUnit.MINUTES);

    }

    @Override
    public void put(UUID key, Funko value) {
        logger.debug("Añadiendo funko a la cache con id: "+key);
        cache.put(key, value);
    }

    @Override
    public Funko get(UUID key) {
        logger.debug("Obteniendo funko de la cache con id: "+key);
        return cache.get(key);
    }

    @Override
    public void remove(UUID key) {
        logger.debug("Eliminando funko de la cache con id: "+key);
        cache.remove(key);
    }

    @Override
    public void clear() {
        logger.debug("Vaciando cache");
        cache.clear();
    }

    @Override
    public void shutdown() {
        logger.info("Cerrando cache");
        executorService.shutdown();
    }


}
