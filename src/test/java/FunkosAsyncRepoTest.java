import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.repositories.FunkosAsyncRepoImpl;


import dev.services.database.DatabaseManager;
import dev.services.generator.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class FunkosAsyncRepoTest {

    private FunkosAsyncRepoImpl funkosAsyncRepo;
    private List<Funko> funkos = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        this.funkosAsyncRepo = FunkosAsyncRepoImpl.getInstance(IdGenerator.getInstance(), DatabaseManager.getInstance());

        funkos.clear();
        funkosAsyncRepo.deleteAll();

        for (int i = 0; i < 12; i++) {
            funkos.add(new Funko(UUID.randomUUID(), i, "Funko "+i, Modelo.values()[i%Modelo.values().length], 20.5, LocalDate.now()));
        }
    }

    @Test
    public void testFindByName() throws ExecutionException, InterruptedException {

        for (Funko funko : funkos) {
            this.funkosAsyncRepo.save(funko);
        }

        Optional<Funko> funko = this.funkosAsyncRepo.findByName("Funko 1").get();
        assertEquals(funko.get().nombre(), "Funko 1");


    }

    @Test
    public void testFindByNameFail() throws ExecutionException, InterruptedException {

        for (Funko funko : funkos) {
            this.funkosAsyncRepo.save(funko);
        }

        Optional<Funko> funko = this.funkosAsyncRepo.findByName("Funko 111").get();
        assertNull(funko.orElse(null));

    }


    @Test
    public void testFindById() throws ExecutionException, InterruptedException {

        for (Funko funko : funkos) {
            this.funkosAsyncRepo.save(funko);
        }

        Optional<Funko> funko = this.funkosAsyncRepo.findById(funkos.get(1).codigo()).get();
        assertEquals(funko.get().nombre(), "Funko 1");

    }

    @Test
    public void testFindByIdFail() throws ExecutionException, InterruptedException {

        for (Funko funko : funkos) {
            this.funkosAsyncRepo.save(funko);
        }

        Optional<Funko> funko = this.funkosAsyncRepo.findById(UUID.randomUUID()).get();
        assertNull(funko.orElse(null));

    }

    @Test
    public void testFindAll() throws ExecutionException, InterruptedException {

        for (Funko funko : funkos) {
            this.funkosAsyncRepo.save(funko).get();
        }

        List<Funko> allFunkos = this.funkosAsyncRepo.findAll().get();
        assertEquals(allFunkos.size(), 12);

    }

    @Test
    public void testSave() throws ExecutionException, InterruptedException {

        Funko funko = this.funkosAsyncRepo.save(funkos.get(0)).get();
        assertEquals(funko.nombre(), "Funko 0");

    }

    @Test
    public void testUpdate() throws ExecutionException, InterruptedException {

        Funko funko = this.funkosAsyncRepo.save(funkos.get(0)).get();
        funko = this.funkosAsyncRepo.update(funko.codigo(), funkos.get(1)).get();
        assertEquals(funko.nombre(), "Funko 1");

    }

    @Test
    public void testDelete() throws ExecutionException, InterruptedException {

        Funko funko = this.funkosAsyncRepo.save(funkos.get(0)).get();
        boolean deleted = this.funkosAsyncRepo.delete(funko.codigo()).get();
        assertAll(
                () -> assertTrue(deleted),
                () -> assertEquals(this.funkosAsyncRepo.findAll().get().size(), 0)
        );

    }

    @Test
    public void testDeleteFail() throws ExecutionException, InterruptedException {

        Funko funko = this.funkosAsyncRepo.save(funkos.get(0)).get();
        boolean deleted = this.funkosAsyncRepo.delete(UUID.randomUUID()).get();
        assertAll(
                () -> assertFalse(deleted),
                () -> assertEquals(this.funkosAsyncRepo.findAll().get().size(), 1)
        );

    }

    @Test
    public void testDeleteAll() throws ExecutionException, InterruptedException {

        for (Funko funko : funkos) {
            this.funkosAsyncRepo.save(funko).get();
        }

        this.funkosAsyncRepo.deleteAll().get();

        System.out.println(this.funkosAsyncRepo.findAll().get().size());

        assertEquals(this.funkosAsyncRepo.findAll().get().size(), 0);

    }

}
