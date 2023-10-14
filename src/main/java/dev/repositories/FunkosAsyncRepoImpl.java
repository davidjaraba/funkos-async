package dev.repositories;

import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.exceptions.FunkoException;
import dev.exceptions.FunkoNoEncontrado;
import dev.exceptions.FunkoNoGuardado;
import dev.services.database.DatabaseManager;
import dev.services.generator.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FunkosAsyncRepoImpl implements FunkosAsyncRepo {

    private static FunkosAsyncRepoImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkosAsyncRepoImpl.class);
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;

    private FunkosAsyncRepoImpl(IdGenerator idGenerator, DatabaseManager databaseMgr) {
        this.databaseManager = databaseMgr;
        this.idGenerator = idGenerator;
    }

    public static synchronized FunkosAsyncRepoImpl getInstance(IdGenerator idGenerator, DatabaseManager databaseMgr){
        if (instance == null){
            instance = new FunkosAsyncRepoImpl(idGenerator, databaseMgr);
        }
        return instance;
    }

    @Override
    public CompletableFuture<Optional<Funko>> findByName(String name) {
        String sql = "SELECT * FROM funkos WHERE nombre = ?";

        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = databaseManager.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql);){

                preparedStatement.setString(1, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return Optional.of(Funko.builder().codigo(UUID.fromString(resultSet.getString("cod")))
                            .myid(resultSet.getInt("myid"))
                            .nombre(resultSet.getString("nombre"))
                            .modelo(Modelo.valueOf(resultSet.getString("modelo")))
                            .precio(resultSet.getDouble("precio"))
                            .fechaLanzamiento(resultSet.getDate("fecha_lanzamiento").toLocalDate())
                            .build());
                }

            } catch (Exception e) {
                logger.error("Error al buscar funko por nombre ", e);
                return Optional.empty();
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<List<Funko>> findAll() {
        String sql = "SELECT * FROM funkos";

        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = databaseManager.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql);){

                List<Funko> funkos = new ArrayList<>();

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {

                    Funko f = Funko.builder().codigo(UUID.fromString(resultSet.getString("cod")))
                            .myid(resultSet.getInt("myid"))
                            .nombre(resultSet.getString("nombre"))
                            .modelo(Modelo.valueOf(resultSet.getString("modelo")))
                            .precio(resultSet.getDouble("precio"))
                            .fechaLanzamiento(resultSet.getDate("fecha_lanzamiento").toLocalDate())
                            .build();

                    funkos.add(f);

                }

                return funkos;

            } catch (Exception e) {
                logger.error("Error al buscar funkos ", e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Funko>> findById(UUID id) {
        String sql = "SELECT * FROM funkos WHERE nombre = ?";

        return CompletableFuture.supplyAsync(()->{
            try (Connection connection = databaseManager.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql);){

                preparedStatement.setString(1, id.toString());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return Optional.of(Funko.builder().codigo(UUID.fromString(resultSet.getString("cod")))
                            .myid(resultSet.getInt("myid"))
                            .nombre(resultSet.getString("nombre"))
                            .modelo(Modelo.valueOf(resultSet.getString("modelo")))
                            .precio(resultSet.getDouble("precio"))
                            .fechaLanzamiento(resultSet.getDate("fecha_lanzamiento").toLocalDate())
                            .build());
                }

            } catch (Exception e) {
                logger.error("Error al buscar funko por id ", e);
                throw new FunkoNoEncontrado("Error al buscar el funko con id "+id.toString());
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Funko> save(Funko entity){
        return CompletableFuture.supplyAsync(()->{

            Funko entityRes;

            try {
                String sql = "INSERT INTO funkos (cod, myid, nombre, modelo, precio, fecha_lanzamiento) VALUES (?, ?, ?, ?, ?, ?)";

                entityRes = Funko.builder().codigo(entity.codigo()).myid(idGenerator.getAndIncrement()).nombre(entity.nombre()).modelo(entity.modelo()).precio(entity.precio()).fechaLanzamiento(entity.fechaLanzamiento()).build();

                int res = databaseManager.executeUpdate(sql, entityRes.codigo(), entityRes.myid(), entityRes.nombre(), entityRes.modelo().toString(), entityRes.precio(), entityRes.fechaLanzamiento());

                if (res == 0) {
                    logger.error("Error al guardar el funko");
                    throw new FunkoNoGuardado("Error al guardar el funko con id "+entity.codigo().toString());
                }

            } catch (Exception e) {
                logger.error("Error al insertar el funko ", e);
                return null;
            }
            return entityRes;
        });
    }

    @Override
    public CompletableFuture<Funko> update(UUID uuid, Funko entity) {
        return CompletableFuture.supplyAsync(()->{
            try{
                String sql = "UPDATE funkos SET nombre = ?, myid = ?, modelo = ?, precio = ?, fecha_lanzamiento = ? WHERE cod = ?";
                int res = databaseManager.executeUpdate(sql, entity.nombre(), entity.myid(), entity.modelo().toString(), entity.precio(), entity.fechaLanzamiento(), uuid);

                if (res == 0) {
                    logger.error("Error al actualizar el funko");
                    throw new FunkoNoGuardado("Error al actualizar el funko con id "+entity.codigo().toString());
                }
            }catch (Exception e){
                logger.error("Error al actualizar el funko ", e);
                return entity;
            }
            return entity;
        });
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID uuid) {
        return CompletableFuture.supplyAsync(()->{
           try{
               String sql = "DELETE FROM funkos WHERE cod = ?";
                int res = databaseManager.executeUpdate(sql, uuid);

                if (res == 0) {
                    logger.error("Error al eliminar el funko");
                    throw new FunkoException("Error al eliminar el funko con id "+uuid.toString());
                }

                return true;

           }catch (Exception e){
               logger.error("Error al actualizar el funko ", e);
               return false;
           }
        });
    }
}
