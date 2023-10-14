package dev.services.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class DatabaseManager {

    private static DatabaseManager instance;

    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final HikariDataSource dataSource;



    public static synchronized DatabaseManager getInstance() {

        if (instance == null) {

            instance = new DatabaseManager();

        }

        return instance;

    }

    public Optional<Connection> connect () {

        try(Connection connection = this.getConnection()){
            return Optional.of(connection);
        }catch (Exception e){
            logger.error("Error al conectarse a la base de datos "+e);
        }

        return Optional.empty();
    }

    private DatabaseManager() {

        String url = "jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1";
        Properties appProps = new Properties();


        try{

            appProps.load(new FileInputStream(getClass().getClassLoader().getResource("application.properties").getPath()));

            url = appProps.getProperty("db.stringDB");


        }catch (Exception e) {
            logger.error("Error al inicializar la base de datos " + e);
        }

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);


        dataSource = new HikariDataSource(config);

        try {
            if (appProps.getProperty("db.loadTables").equals("true")){

                initializeDB(appProps.getProperty("db.initScript"));

            }
        }catch (Exception e) {
            logger.error("Error al inicializar la base de datos " + e);
        }

    }


    public void initializeDB(String initScriptPath) throws FileNotFoundException, SQLException {

        try(Connection connection = this.getConnection()){
            Reader reader = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(initScriptPath).getPath()));
            ScriptRunner sr = new ScriptRunner(connection);
            sr.runScript(reader);
        }

    }

    public int executeUpdate(String sqlSentence, Object... params){

        try(Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sqlSentence)){

            Arrays.stream(params).forEach(param -> {
                try {
                    preparedStatement.setObject(Arrays.asList(params).indexOf(param)+1, param);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            return preparedStatement.executeUpdate();

        }catch (Exception e){
            logger.error("Error al lanzar la consultar SQL", e);
        }

        return 0;
    }


    public synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
