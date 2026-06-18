package com.sintropia.calculator.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.strategy.DashboardExporter;

@Service
public class DashboardExportService {

	private final Map<String, DashboardExporter> exporters;
	
	public DashboardExportService(List<DashboardExporter> exporterList) {
        this.exporters = exporterList.stream()
            .collect(Collectors.toMap(DashboardExporter::getDashboardId, Function.identity()));
    }
	
	public byte[] export(String dashboardId, String format, UserDTO user) throws Exception {
        DashboardExporter exporter = exporters.get(dashboardId);
        
        if (exporter == null) {
            throw new IllegalArgumentException("Selecione um dashboard valido");
        }

        if ("excel".equalsIgnoreCase(format)) {
            return exporter.exportExcel(user);
        } else if ("pdf".equalsIgnoreCase(format)) {
            return exporter.exportPdf(user);
        } else {
            throw new IllegalArgumentException("Formato inválido: " + format);
        }
    }
	
}
