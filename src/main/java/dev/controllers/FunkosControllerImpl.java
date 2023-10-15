package dev.controllers;

import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.services.FunkosServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FunkosControllerImpl {


    FunkosServiceImpl funkosService;


    public FunkosControllerImpl(FunkosServiceImpl funkosService) {
        this.funkosService = funkosService;
    }

    public Optional<Funko> mostExpensiveFunko() throws ExecutionException, InterruptedException {
        return funkosService.mostExpensiveFunko();
    }

    public double averagePrice() throws ExecutionException, InterruptedException {
        return funkosService.averagePrice();
    }

    public Map<Modelo, List<Funko>> funkosGroupedByModel() throws ExecutionException, InterruptedException {
        return funkosService.funkosGroupedByModel();
    }

    public Map<Modelo, Integer> numFunkosGroupedByModel() throws ExecutionException, InterruptedException {
        return funkosService.numFunkosGroupedByModel();
    }

    public List<Funko> funkosReleasedInYear(int year) throws ExecutionException, InterruptedException {
        return funkosService.funkosReleasedInYear(year);
    }

    public int numFunkosContainWord(String word) throws ExecutionException, InterruptedException {
        return funkosService.funkosContainWord(word).size();
    }

    public List<Funko> funkosContainWord(String word) throws ExecutionException, InterruptedException {
        return funkosService.funkosContainWord(word);
    }





}
