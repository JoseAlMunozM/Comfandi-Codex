package com.comfandi.korlon.mapper;

import com.comfandi.korlon.manager.data.DataSet;
import com.comfandi.korlon.manager.data.DatosPlantilla;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class DataSetMapper {

    public DataSet datosPlantillaToDataSet(DatosPlantilla datosPlantilla){
        DataSet dataSet= new DataSet();
        dataSet.setRegional(datosPlantilla.getRegional());
        dataSet.setCebe(datosPlantilla.getCebe());
        dataSet.setMode(datosPlantilla.getMode());
        dataSet.setCant(datosPlantilla.getCant());
        dataSet.setSum(datosPlantilla.getSum());
        return dataSet;
    }

    public List<DataSet> datosPlantillaListToDataSetList(List<DatosPlantilla> datosPlantillaList){
        return datosPlantillaList.stream().map(this::datosPlantillaToDataSet).toList();
    }
}
