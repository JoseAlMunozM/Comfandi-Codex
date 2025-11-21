package com.comfandi.korlon.services;

import com.comfandi.korlon.entities.CountableDataEntity;
import com.comfandi.korlon.entities.TypificationEntity;
import com.comfandi.korlon.manager.data.DatosPlantilla;
import com.comfandi.korlon.manager.data.InfoPlantillaContableRow;
import com.comfandi.korlon.manager.data.InfoValidacionPlantillaRow;
import com.comfandi.korlon.utils.LoggerKorlon;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InfoValidacionPlantillaService {
    private Logger Log = LoggerKorlon.logger;
    private final int internalGroups=2;

    @Autowired
    private CountableDataService countableDataService;

    public List<InfoValidacionPlantillaRow> generarDatosValidacionPlantilla(List<DatosPlantilla> datosPlantilla, List<TypificationEntity> typificationEntityList,String profile){
        try {
            List<InfoValidacionPlantillaRow> infoValidacionPLantillaRows = new ArrayList<>();
            for (int j= 1;j<= internalGroups; j++) {
                for (DatosPlantilla plantillaRow : datosPlantilla) {
                    CountableDataEntity countableDataEntity = countableDataService
                            .getCountableDataByParameters(plantillaRow.getRegional(),profile,1,j);
                    InfoValidacionPlantillaRow newRow = new InfoValidacionPlantillaRow();
                    TypificationEntity typification = typificationEntityList.stream()
                            .filter(x-> x.getLocation().equals(plantillaRow.getRegional()))
                            .filter(x-> x.getType().equals(profile))
                            .findAny().orElse(null);
                    newRow.setItem("1");
                    newRow.setClave(""+countableDataEntity.getCountableKey());
                    newRow.setCebe(countableDataEntity.isCebe()?typification.getCebe():countableDataEntity.getBeneficiaryCenter());
                    newRow.setAsignation(plantillaRow.getAssignation());
                    newRow.setAccount(plantillaRow.getAccount());
                    newRow.setSumValue(plantillaRow.getSum());
                    newRow.setText(plantillaRow.getTextRegistry());
                    infoValidacionPLantillaRows.add(newRow);
                }
                //add total
            }

            return infoValidacionPLantillaRows;
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Error on generate validacion planilla: " + e.getMessage());
        }
        return Collections.emptyList();

    }
}
