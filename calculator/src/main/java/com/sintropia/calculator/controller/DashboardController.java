package com.sintropia.calculator.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.dto.response.DashboardDTO;
import com.sintropia.calculator.mapper.UserMapper;
import com.sintropia.calculator.service.CalculatorService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("/api/dashboards")
public class DashboardController {

	private final UserService userService;
	private final CalculatorService calculatorService;

	public DashboardController(UserService userService,CalculatorService calculatorService) {
		this.userService = userService;
		this.calculatorService = calculatorService;
	}

	@GetMapping
	public List<DashboardDTO> listDashboards() {
		return List.of(
			new DashboardDTO("emissao-prevista", "Emissão Prevista", "/")
		);
	}

	@GetMapping("/{id}/export")
	public ResponseEntity<byte[]> exportDashboard(
			@PathVariable String id,
			@RequestParam String format,
			@AuthenticationPrincipal String email) throws Exception {

		if (!"emissao-prevista".equals(id)) {
			return ResponseEntity.notFound().build();
		}

		if (!"excel".equals(format)) {
			return ResponseEntity.badRequest().build();
		}

		UserDTO user = userService.findByEmail(email);
		CalculationResponseDTO data = calculatorService.calculate(user);
		byte[] excelBytes = generateExcel(data);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition.attachment().filename("dashboard.xlsx").build());

		return ResponseEntity.ok()
			.headers(headers)
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(excelBytes);
	}

	private byte[] generateExcel(CalculationResponseDTO data) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Emissão Prevista");

			String[][] rows = {
				{"Indicador", "Valor"},
				{"Emissão anual - cartão físico (kg CO2)", String.valueOf(data.annualPhysicEmission())},
				{"Emissão anual - cartão digital (kg CO2)", String.valueOf(data.annualDigitalEmission())},
				{"Dinheiro gasto em produção (R$)", String.valueOf(data.moneyWasted())},
				{"Emissão de transporte (%)", String.valueOf(data.transportEmissionPercentage())},
				{"Emissão de produção (%)", String.valueOf(data.productionEmissionPercentage())},
				{"Emissão de descarte (%)", String.valueOf(data.disposalEmissionPercentage())},
				{"Número de cartões", String.valueOf(data.cardCount())}
			};

			for (int i = 0; i < rows.length; i++) {
				Row row = sheet.createRow(i);
				for (int j = 0; j < rows[i].length; j++) {
					row.createCell(j).setCellValue(rows[i][j]);
				}
			}

			for (int j = 0; j < 2; j++) {
				sheet.autoSizeColumn(j);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		}
	}
}