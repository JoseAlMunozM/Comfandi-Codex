package com.comfandi.korlon.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.comfandi.korlon.manager.data.BusinessData;

@Repository
public class BusinessRepository {
 public List<BusinessData> findAll() {
        List<BusinessData> list = new ArrayList<>();
        list.add(new BusinessData(
                "123",
                "MACROTICS S.A.S.",
                "12323",
                "NombreRepresentanteLegal",
                "PruebaDireccion",
                "423232323",
                "CALI",
                "cesar.munoz@olsoftware.com",
                "Amazonas",
                "Programa de sostenibilidad",
                "1000",
                "CALI",
                "2025-08-29T19:17:07.316Z",
                "2025-09-01T17:11:54.592Z",
                "2.6",
                "2025-09-01T17:10:30.995Z",
                "2025-09-01T17:10:30.995Z",
                "Desarrollo Empresarial",
                "2025-08-29T19:17:07.316Z",
                "Cobro",
                " ",
                " ",
                0L,
                0.0
        ));
        return list;
    }
}
