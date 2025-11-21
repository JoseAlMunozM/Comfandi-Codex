package com.comfandi.korlon.services;

import java.util.*;

import com.comfandi.korlon.entities.CountableDataEntity;
import com.comfandi.korlon.entities.TypificationEntity;
import com.comfandi.korlon.manager.data.AccountGroup;
import com.comfandi.korlon.manager.data.DatosPlantilla;
import com.comfandi.korlon.manager.data.DatosPlantillaHeader;
import com.comfandi.korlon.manager.data.InfoPlantillaContableRow;
import org.slf4j.Logger;

import com.comfandi.korlon.utils.LoggerKorlon;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class InfoPlantillaContableService {

	private Logger Log = LoggerKorlon.logger;

	// Document Group { 1, 2. 3 }
	// Account Group { 40, 50 }

	private final int internalGroups=2;


	private ArrayList<AccountGroup> accountGroups;
    @Autowired
    private CountableDataService countableDataService;


	public Map<Integer,List<InfoPlantillaContableRow>> generatePlanillaContable(List<DatosPlantilla> datosPlantilla,
																				List<TypificationEntity> typificationEntityList, DatosPlantillaHeader header, String profile,Boolean amortization) {
		try {
			int maxGroups= profile.equals("fomento-th-fosfec")?1:3;
			if(amortization){
				maxGroups=1;
			}
			final String CLASE_DOCUMENTO = profile.equals("fomento-th-fosfec")?"W1":"SA";
			final String SOCIEDAD = "1000";
			final String MONEDA = "COP";
			Map<Integer,List<InfoPlantillaContableRow>> tableRows = new HashMap<>();
			for (int i= 1;i<= maxGroups; i++) {
				List<InfoPlantillaContableRow> infoPlantillaContableRows = new ArrayList<>();
				int groupElementCount = 1;
				for (DatosPlantilla plantillaRow : datosPlantilla) {
					for (int j= 1;j<= internalGroups; j++) {
						CountableDataEntity countableDataEntity = countableDataService
								.getCountableDataByParameters(plantillaRow.getRegional(),profile,i,j);
						InfoPlantillaContableRow newRow = new InfoPlantillaContableRow();
						TypificationEntity typification = typificationEntityList.stream()
								.filter(x-> x.getLocation().equals(plantillaRow.getRegional()))
										.filter(x-> x.getType().equals(profile))
												.findAny().orElse(null);

						newRow.setConsecutivoDelDocumento(String.valueOf(i));
						newRow.setFechaDocumento(header!=null?header.getDate():"");
						newRow.setClaseDocumento(CLASE_DOCUMENTO);
						newRow.setSociedad(SOCIEDAD);
						newRow.setFechaContabilidad(header!=null?header.getDate():"");
						newRow.setMoneda(MONEDA);
						newRow.setReferencia(header!=null?header.getConcept():"");
						newRow.setTextoCabeceraDoc(header!=null?header.getConcept():"");
						newRow.setNumeroDeApunteContableDentroDelDocumento(String.valueOf(groupElementCount));
						newRow.setClaveDeContabilidad(""+countableDataEntity.getCountableKey());
						newRow.setCuentaDeMayor(countableDataEntity.isAccount()?typification.getAccount():countableDataEntity.getMajorAccount());
						newRow.setValorDeImporte(String.valueOf(plantillaRow.getSum()));
						newRow.setNumeroDeAsignacion(header!=null?header.getProgram():"");
						newRow.setTexto(plantillaRow.getTextRegistry());
						newRow.setCentroDeBeneficio(countableDataEntity.isCebe()?typification.getCebe():countableDataEntity.getBeneficiaryCenter());

						infoPlantillaContableRows.add(newRow);
						groupElementCount++;
					}
				}
				tableRows.put(i,infoPlantillaContableRows);
			}
			return tableRows;
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Error on generate datos planilla: " + e.getMessage());
		}
		return Collections.emptyMap();
	}

}
