package com.comfandi.korlon.services;

import java.util.*;
import java.util.stream.Collectors;

import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.entities.PortfolioEntity;
import com.comfandi.korlon.entities.TypificationEntity;
import com.comfandi.korlon.manager.data.*;
import org.slf4j.Logger;

import com.comfandi.korlon.utils.LoggerKorlon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
public class InfoDatosPlantillaService {
	
	Logger Log = LoggerKorlon.logger;

	private String generateTextregistry(String consept, String conseptObject, long sumaCedula, String modalidadResumida, String programa) {
		return String.format("%s %d %s %s %s", consept, sumaCedula, conseptObject, modalidadResumida, programa );
	}
	private String generateTextregistryAmortization(String consept, String conseptObject, long sumaCedula, String modalidadResumida, String programa,String etapa) {
		return String.format("%s %d %s %s %s %s", consept, sumaCedula,etapa, conseptObject, modalidadResumida, programa );
	}

	public List<DatosPlantilla> generateDatosPLantillaTH(ArrayList<ApiInformationDatabase> data, List<TypificationEntity> typificationEntityList,
														 DatosPlantillaHeader header,Boolean amortizable, List<PortfolioEntity> portfolioList){
		try {
			Map<DatosPlantillaGroupTH, CountableData> groupSum = data.stream()
					.collect(Collectors.groupingBy(
							k -> new DatosPlantillaGroupTH(
									k.getRegionalCobro(),
									k.getNombreDelPrograma(),
									k.getModalidad(),
									k.getPortfolioId()
							)
					))
					.entrySet()
					.stream()
					.collect(Collectors.toMap(
							Map.Entry::getKey,
							entry -> {
								List<ApiInformationDatabase> list = entry.getValue();
								double sum = list.stream()
										.mapToDouble(v -> Double.parseDouble(v.getValor()))
										.sum();
								long count = list.size();

								CountableData cd = new CountableData();
								cd.setPortfolioId(entry.getKey().getPortfolioId());
								cd.setSum(sum);
								cd.setCant(count);
								return cd;
							}
					));
		List<DatosPlantilla> plantillas = groupSum.entrySet().stream()
				.map(dataGroup-> {
					DatosPlantillaGroupTH  group = dataGroup.getKey();
					CountableData valor = dataGroup.getValue();
					String textRegistry=amortizable?this.generateTextregistryAmortization(header!=null?header.getConcept():"",
							header!=null?header.getConceptObject():"",
							valor.getCant(),
							group.getMode().substring(0, 4), group.getProgram(),header!=null? header.getEtapa() : ""):this.generateTextregistry(header!=null?header.getConcept():"",
							header!=null?header.getConceptObject():"",
							valor.getCant(),
							group.getMode().substring(0, 4), group.getProgram());
					TypificationEntity defaultTypification = TypificationEntity
							.builder().account("2705950132")
							.assignment("ACTIVOS - EMPRENDI")
							.type("activos-empresarial").location("CALI").cebe("F050011900").build();
					TypificationEntity typificationEntity = typificationEntityList.stream()
							.filter(x -> x.getLocation().equalsIgnoreCase(group.getRegional())).findFirst().orElse(defaultTypification);
					if(amortizable){
						Optional<PortfolioEntity> portfolio=portfolioList.stream().filter(x->x.getId().toString().equals(group.getPortfolioId())).findFirst();
						return DatosPlantilla.builder()
								.regional(group.getRegional())
								.program(group.getProgram())
								.mode(group.getMode())
								.sum(valor.getSum())
								.cant(valor.getCant())
								.textRegistry(textRegistry)
								.textLength(textRegistry.length())
								.account(typificationEntity.getAccount())
								.cebe(typificationEntity.getCebe())
								.resume(group.getMode().substring(0, 4))
								.assignation(typificationEntity.getAssignment())
								.downPayment1(portfolio.map(PortfolioEntity::getDownPayment1).orElse(null))
								.downPayment2(portfolio.map(PortfolioEntity::getDownPayment2).orElse(null))
								.downPayment3(portfolio.map(PortfolioEntity::getDownPayment3).orElse(null))
								.build();
					}else{
						return DatosPlantilla.builder()
								.regional(group.getRegional())
								.program(group.getProgram())
								.mode(group.getMode())
								.sum(valor.getSum())
								.cant(valor.getCant())
								.textRegistry(textRegistry)
								.textLength(textRegistry.length())
								.account(typificationEntity.getAccount())
								.cebe(typificationEntity.getCebe())
								.resume(group.getMode().substring(0, 4))
								.assignation(typificationEntity.getAssignment())
								.build();
					}

				}).toList();
		return plantillas;
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Error on generate datos planilla: " + e.getMessage());
		}
		return new ArrayList<>();
	}
	public List<DatosPlantilla> generateDatosPlanilla(ArrayList<ApiInformationDatabase> data, List<TypificationEntity> typificationEntityList, DatosPlantillaHeader header) {
		try {

			Map<DatosPlantillaGroup,AbstractMap.SimpleEntry<Double, Long>> groupSum = data.stream()
					.collect(Collectors.groupingBy(k ->
							new DatosPlantillaGroup(k.getRegionalCobro(),
									k.getModalidad()),
									Collectors.teeing(
											Collectors.summingDouble(v-> Double.parseDouble(v.getValor())),
											Collectors.counting(),
											AbstractMap.SimpleEntry::new
									)
							));

			List<DatosPlantilla> plantillas = groupSum.entrySet().stream()
					.map(dataGroup -> {
						DatosPlantillaGroup  group = dataGroup.getKey();
						AbstractMap.SimpleEntry<Double, Long> valor = dataGroup.getValue();
						String textRegistry=this.generateTextregistry(header!=null?header.getConcept():"",
								header!=null?header.getConceptObject():"",
								valor.getValue(),
								group.getMode().substring(0, 4), header!=null?header.getProgram():"");
						TypificationEntity defaultTypification = TypificationEntity
								.builder().account("2705950132")
								.assignment("ACTIVOS - EMPRENDI")
								.type("activos-empresarial").location("CALI").cebe("F050011900").build();
						TypificationEntity typificationEntity = typificationEntityList.stream().filter(x -> x.getLocation().equalsIgnoreCase(group.getRegional())).findFirst().orElse(defaultTypification);
                        return DatosPlantilla.builder()
                                .regional(group.getRegional())
                                .program(header!=null?header.getProgram():"")
								.mode(group.getMode())
								.sum(valor.getKey())
								.cant(valor.getValue())
								.textRegistry(textRegistry)
								.textLength(textRegistry.length())
								.account(typificationEntity.getAccount())
								.cebe(typificationEntity.getCebe())
								.resume(group.getMode().substring(0, 4))
								.assignation(typificationEntity.getAssignment())
								.build();
                    }).toList();
			return plantillas;
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Error on generate datos planilla: " + e.getMessage());	
		}		
		return new ArrayList<>();
		
	}
	
	
	
}
