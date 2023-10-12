package dev;

import dev.repositories.FunkosAsyncRepoImpl;
import dev.services.database.DatabaseManager;
import dev.services.generator.IdGenerator;
import dev.services.storage.FunkosStorageImpl;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        IdGenerator idGenerator = new IdGenerator();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        FunkosAsyncRepoImpl funkosAsyncRepoImpl = FunkosAsyncRepoImpl.getInstance(idGenerator, databaseManager);
        FunkosStorageImpl funkosStorage = new FunkosStorageImpl(funkosAsyncRepoImpl);

        funkosStorage.importCSVToDB();




    }
}