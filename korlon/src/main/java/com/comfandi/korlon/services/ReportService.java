package com.comfandi.korlon.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.comfandi.korlon.manager.data.BusinessData;
import com.comfandi.korlon.repositories.BusinessRepository;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportService {

    private final BusinessRepository businessRepository;

    public ReportService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public byte[] exportCuentaCobroPdf() throws Exception {
        JasperReport jasperReport = JasperCompileManager.compileReport(
            getClass().getResourceAsStream("/jasper/comfandi.jrxml")
        );

        List<BusinessData> businesses = businessRepository.findAll();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(businesses);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cuentaCobro", "001-2025");
        parameters.put("fecha", "09/09/2025");
        parameters.put("cebe", "99999");
        parameters.put("cuentaContable", "110505");
        parameters.put("totalEnLetras", "Seiscientos setenta y cinco mil pesos m/cte");
        parameters.put("concepto", "Pago correspondiente al mecanismo de protección al cesante.");
        parameters.put("cuentaContable2", "220505");
        parameters.put("nombreFirma", "Juan Pérez");
        parameters.put("cargoFirma", "Director Financiero");
        parameters.put("imageLogo", getClass().getResourceAsStream("src/main/resources/jasper/comfandiLogo.png"));
        parameters.put("nombreRevisor", "Ana Torres");
        parameters.put("nombreAprobacion", "Luis Gómez");

        double total = businesses.stream()
            .mapToDouble(b -> {
                try {
                    return Double.parseDouble(
                        b.getPriceValue() == null ? "0" : b.getPriceValue()
                    );
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            })
            .sum();

        parameters.put("total", total);
        parameters.put("datosPlantilla", dataSource);

        JasperPrint jasperPrint = JasperFillManager.fillReport(
            jasperReport,
            parameters,
            new JREmptyDataSource()
        );

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}