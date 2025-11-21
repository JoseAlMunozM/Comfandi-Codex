package com.comfandi.korlon.repositories;

import com.comfandi.korlon.entities.BillingAccountEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PendingRepositoryTest {

    @Autowired
    private PendingRepository pendingRepository;

    @BeforeEach
    void setUp() {
        pendingRepository.deleteAll();

        pendingRepository.save(new BillingAccountEntity(null, "Empresa 1", "Tipo A", "Ley X", "wordUrl1", "excelUrl1", true, false));
        pendingRepository.save(new BillingAccountEntity(null, "Empresa 2", "Tipo B", "Ley Y", "wordUrl2", "excelUrl2", true, true));
        pendingRepository.save(new BillingAccountEntity(null, "Empresa 3", "Tipo C", "Ley Z", "wordUrl3", "excelUrl3", false, false));
    }

    @Test
    void testFindByCompleteFalse() {
        List<BillingAccountEntity> incompleteAccounts = pendingRepository.findByCompleteFalse();

        assertNotNull(incompleteAccounts);
        assertEquals(2, incompleteAccounts.size());
        assertTrue(incompleteAccounts.stream().allMatch(a -> !a.getComplete()));
    }
}
