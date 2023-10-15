package dev.services.cache;

public interface FunkosCache<UUID, Funko> {

    void put(UUID key, Funko value);

    Funko get(UUID key);

    void remove(UUID key);

    void clear();

    void shutdown();

}
