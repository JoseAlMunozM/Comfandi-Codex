package com.comfandi.korlon.repositories;

import com.comfandi.korlon.manager.data.BusinessData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusinessRepositoryTest {

    private BusinessRepository businessRepository;

    @BeforeEach
    void setUp() {
        businessRepository = new BusinessRepository();
    }

    @Test
    void testFindAll() {
        List<BusinessData> result = businessRepository.findAll();

        assertNotNull(result, "La lista no debe ser nula");
        assertEquals(1, result.size(), "Debe haber exactamente un elemento");

        BusinessData business = result.get(0);
        assertEquals("123", business.getNit());
        assertEquals("MACROTICS S.A.S.", business.getBusinessName());
        assertEquals("12323", business.getRepresentativeDocument());
        assertEquals("NombreRepresentanteLegal", business.getRepresentativeName());
        assertEquals("CALI", business.getCity());
        assertEquals("Cobro", business.getTemplateType());
    }
}
