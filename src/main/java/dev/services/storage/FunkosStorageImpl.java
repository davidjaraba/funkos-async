package dev.services.storage;

import com.google.gson.GsonBuilder;
import dev.adapters.LocalDateAdapter;
import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.repositories.FunkosAsyncRepoImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import com.google.gson.Gson;


public class FunkosStorageImpl {


    private final String CSV_SEPARATOR = ",";
    private final FunkosAsyncRepoImpl repository;

    public FunkosStorageImpl(FunkosAsyncRepoImpl repository) {
        this.repository = repository;
    }


    private String getDataDir() {

        String userDir = System.getProperty("user.dir");
        Path currentRelativePath = Paths.get(userDir);
        String ruta = currentRelativePath.toAbsolutePath().toString();
        return ruta + File.separator + "data";
    }

    public void importCSVToDB() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        String dir = getDataDir();
        String funkosCSV = dir+File.separator+ "funkos.csv";
        Path filePath = Paths.get(funkosCSV);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + funkosCSV);
        }

        CompletableFuture<List<Funko>> funkosFuture = CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                return reader.lines()
                        .skip(1)
                        .map(line -> {
                            String[] fields = line.split(CSV_SEPARATOR);
                            String uuid = fields[0];
                            return new Funko(UUID.fromString(uuid.substring(0, 35)), 0, fields[1], Modelo.valueOf(fields[2]), Double.parseDouble(fields[3]), LocalDate.parse(fields[4]));
                        })
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo CSV", e);
            }
        }, executorService);

        funkosFuture.thenAccept(funkos -> {
            for (Funko funko : funkos) {
                repository.save(funko);
            }
        });

        try {
            funkosFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

    }

    public boolean exportDBToJSON() {

        String dir = getDataDir();
        String funkosCSV = dir+File.separator+ "funkos.json";

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        CompletableFuture.supplyAsync(() -> {

            try {

                List<Funko> funkos = repository.findAll().get();

                Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

                String json = gson.toJson(funkos);

                Files.writeString(new File(funkosCSV).toPath(), json);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException | IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        },executorService);

        executorService.shutdown();

        return true;

    }


}
