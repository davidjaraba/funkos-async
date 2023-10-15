import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.repositories.FunkosAsyncRepoImpl;
import dev.services.FunkosServiceImpl;
import dev.services.cache.FunkosCacheImpl;
import dev.services.storage.FunkosStorageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FunkosServiceTest {

    @Mock
    FunkosAsyncRepoImpl funkosAsyncRepo;

    @Mock
    FunkosCacheImpl cache;

    @Mock
    FunkosStorageImpl storage;

    @InjectMocks
    FunkosServiceImpl service;

    private List<Funko> funkos = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        funkos.clear();

        for (int i = 0; i < 12; i++) {
            funkos.add(new Funko(UUID.randomUUID(), i, "Funko "+i, Modelo.values()[i%Modelo.values().length], 20.5+i, LocalDate.of(2017+i, 1, 1)));
        }
    }

    @Test
    public void findAllTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        List<Funko> funkos = service.findAll();

        assertAll(
                ()-> assertEquals(12, funkos.size()),
                ()-> assertEquals("Funko 0", funkos.get(0).nombre(), "El funko no es el esperado"),
                ()-> assertEquals("Funko 11", funkos.get(11).nombre(), "El funko no es el esperado")
        );

        Mockito.verify(funkosAsyncRepo, times(1)).findAll();
    }

    @Test
    public void findByNameTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findByName("Funko 1")).thenReturn(CompletableFuture.completedFuture(Optional.of(funkos.get(1))));

        Funko funko = service.findByName("Funko 1").get();

        assertEquals("Funko 1", funko.nombre(), "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).findByName("Funko 1");
    }

    @Test
    public void findByNameTestFail() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findByName("Funko 111")).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        Funko funko = service.findByName("Funko 111").orElse(null);

        assertNull(funko, "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).findByName("Funko 111");
    }

    @Test
    public void findByIdTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findById(funkos.get(1).codigo())).thenReturn(CompletableFuture.completedFuture(Optional.of(funkos.get(1))));

        Funko funko = service.findById(funkos.get(1).codigo()).get();

        assertEquals("Funko 1", funko.nombre(), "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).findById(funkos.get(1).codigo());
    }

    @Test
    public void findByIdTestFail() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findById(funkos.get(1).codigo())).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        Funko funko = service.findById(funkos.get(1).codigo()).orElse(null);

        assertNull(funko, "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).findById(funkos.get(1).codigo());
    }

    @Test
    public void saveTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.save(funkos.get(1))).thenReturn(CompletableFuture.completedFuture(funkos.get(1)));

        Funko funko = service.save(funkos.get(1));

        assertEquals("Funko 1", funko.nombre(), "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).save(funkos.get(1));
    }

    @Test
    public void updateTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.update(funkos.get(1).codigo(), funkos.get(1))).thenReturn(CompletableFuture.completedFuture(funkos.get(1)));

        Funko funko = service.update(funkos.get(1).codigo(), funkos.get(1));

        assertEquals("Funko 1", funko.nombre(), "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).update(funkos.get(1).codigo(), funkos.get(1));
    }

    @Test
    public void deleteTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.delete(funkos.get(1).codigo())).thenReturn(CompletableFuture.completedFuture(true));

        boolean deleted = service.delete(funkos.get(1).codigo());

        assertTrue(deleted, "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).delete(funkos.get(1).codigo());
    }

    @Test
    public void mostExpensiveFunkoTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        Optional<Funko> funko = service.mostExpensiveFunko();

        assertEquals(funko.get(), funkos.get(11), "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).findAll();
    }

    @Test
    public void averagePriceTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        double average = service.averagePrice();

        assertEquals(average, 26.0, "El funko no es el esperado");

        Mockito.verify(funkosAsyncRepo, times(1)).findAll();
    }

    @Test
    public void funkosGroupedByModelTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        Map<Modelo, List<Funko>> funkos = service.funkosGroupedByModel();

        assertAll(
                ()-> assertEquals(4, funkos.size()),
                ()-> assertEquals(3, funkos.get(Modelo.MARVEL).size(), "El funko no es el esperado")
        );

        Mockito.verify(funkosAsyncRepo, times(1)).findAll();
    }

    @Test
    public void backupTest() {

        when(storage.exportDBToJSON()).thenReturn(true);

        boolean exported = service.backup();

        assertTrue(exported, "Error al exportar");

        Mockito.verify(storage, times(1)).exportDBToJSON();
    }

    @Test
    public void numFunkosGroupedByModelTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        Map<Modelo, Integer> funkos = service.numFunkosGroupedByModel();

        assertAll(
                ()-> assertEquals(4, funkos.size()),
                ()-> assertEquals(3, funkos.get(Modelo.MARVEL), "El funko no es el esperado")
        );

        Mockito.verify(funkosAsyncRepo, times(1)).findAll();
    }

    @Test
    public void funkosReleasedInYearTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        List<Funko> funkos = service.funkosReleasedInYear(2021);

        assertAll(
                ()-> assertEquals(1, funkos.size()),
                ()-> assertEquals(2021, funkos.get(0).fechaLanzamiento().getYear(), "Error en el año")
        );

        Mockito.verify(funkosAsyncRepo, times(1)).findAll();
    }

    @Test
    public void funkosContainWordTest() throws ExecutionException, InterruptedException {

        when(funkosAsyncRepo.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        List<Funko> funkos = service.funkosContainWord("1");

        assertAll(
                ()-> assertEquals(3, funkos.size()),
                ()-> assertEquals("Funko 1", funkos.get(0).nombre(), "Error al obtener funkos que tengan un 1 en el nombre")
        );

        Mockito.verify(funkosAsyncRepo, times(1)).findAll();
    }



}
