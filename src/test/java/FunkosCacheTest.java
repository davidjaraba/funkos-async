import dev.database.models.Funko;
import dev.database.models.Modelo;
import dev.services.cache.FunkosCacheImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FunkosCacheTest {

        private FunkosCacheImpl cache;
        private List<Funko> funkos = new ArrayList<>();

        @BeforeEach
        void setUp() {
            cache = new FunkosCacheImpl();

            for (int i = 0; i < 12; i++) {
                funkos.add(new Funko(UUID.randomUUID(), i, "Funko "+i, Modelo.values()[i%Modelo.values().length], 20.5, LocalDate.now()));
            }

        }

        @Test
        public void testPutGet() {

            for (int i = 0; i < 12; i++) {
                cache.put(funkos.get(i).codigo(), funkos.get(i));
            }

            assertAll(
                    () -> assertEquals(funkos.get(11), cache.get(funkos.get(11).codigo())),
                    () -> assertNull(cache.get(funkos.get(0).codigo()))
            );

        }

        @Test
        public void testRemove(){

            for (int i = 0; i < 12; i++) {
                cache.put(funkos.get(i).codigo(), funkos.get(i));
            }

            cache.remove(funkos.get(11).codigo());

            assertAll(
                    () -> assertNull(cache.get(funkos.get(11).codigo())),
                    () -> assertEquals(funkos.get(10), cache.get(funkos.get(10).codigo()))
            );

        }

        @Test
        public void testClear(){

            for (int i = 0; i < 12; i++) {
                cache.put(funkos.get(i).codigo(), funkos.get(i));
            }

            cache.clear();

            assertAll(
                    () -> assertNull(cache.get(funkos.get(11).codigo())),
                    () -> assertNull(cache.get(funkos.get(10).codigo()))
            );

        }



}
