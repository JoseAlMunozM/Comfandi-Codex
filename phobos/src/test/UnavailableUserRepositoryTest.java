package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.UnavailableUserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UnavailableUserRepositoryTest {

    @Autowired
    private UnavailableUserRepository unavailableUserRepository;

    @BeforeEach
    void setUp() {
        unavailableUserRepository.deleteAll();

        // Insertamos datos de prueba
        unavailableUserRepository.save(new UnavailableUserEntity(null, "User1", LocalDate.of(2025,8,10)));
        unavailableUserRepository.save(new UnavailableUserEntity(null, "User2", LocalDate.of(2025,8,15)));
        unavailableUserRepository.save(new UnavailableUserEntity(null, "User3", LocalDate.of(2025,9,1)));
    }

    @Test
    void testFindByDates() {
        LocalDate start = LocalDate.of(2025,8,1);
        LocalDate end = LocalDate.of(2025,8,31);

        List<UnavailableUserEntity> result = unavailableUserRepository.findByDates(start, end);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> 
                !u.getUnavailableDate().isBefore(start) && !u.getUnavailableDate().isAfter(end)));
    }
}
