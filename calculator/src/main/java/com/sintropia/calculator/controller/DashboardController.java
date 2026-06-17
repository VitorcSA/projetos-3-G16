package com.sintropia.calculator.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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

		byte[] fileBytes;
		String filename;
		MediaType mediaType;

		switch (format) {
			case "excel" -> {
				fileBytes = generateExcel(data);
				filename = "dashboard.xlsx";
				mediaType = MediaType.APPLICATION_OCTET_STREAM;
			}
			case "pdf" -> {
				fileBytes = generatePdf(data);
				filename = "dashboard.pdf";
				mediaType = MediaType.APPLICATION_PDF;
			}
			default -> {
				return ResponseEntity.badRequest().build();
			}
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

		return ResponseEntity.ok()
			.headers(headers)
			.contentType(mediaType)
			.body(fileBytes);
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

	private byte[] generatePdf(CalculationResponseDTO data) throws IOException {
		String[][] rows = {
			{"Indicador", "Valor"},
			{"Emissao anual - cartao fisico (kg CO2)", String.valueOf(data.annualPhysicEmission())},
			{"Emissao anual - cartao digital (kg CO2)", String.valueOf(data.annualDigitalEmission())},
			{"Dinheiro gasto em producao (R$)", String.valueOf(data.moneyWasted())},
			{"Emissao de transporte (%)", String.valueOf(data.transportEmissionPercentage())},
			{"Emissao de producao (%)", String.valueOf(data.productionEmissionPercentage())},
			{"Emissao de descarte (%)", String.valueOf(data.disposalEmissionPercentage())},
			{"Numero de cartoes", String.valueOf(data.cardCount())}
		};

		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			float margin = 50;
			float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
			float col1Width = tableWidth * 0.65f;
			float rowHeight = 25;
			float y = page.getMediaBox().getHeight() - margin;

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {
				content.setFont(PDType1Font.HELVETICA_BOLD, 16);
				content.beginText();
				content.newLineAtOffset(margin, y);
				content.showText("Relatorio - Emissao Prevista");
				content.endText();

				y -= 40;

				for (int i = 0; i < rows.length; i++) {
					content.setFont(i == 0 ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 11);

					content.beginText();
					content.newLineAtOffset(margin, y);
					content.showText(rows[i][0]);
					content.endText();

					content.beginText();
					content.newLineAtOffset(margin + col1Width, y);
					content.showText(rows[i][1]);
					content.endText();

					y -= rowHeight;
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.save(out);
			return out.toByteArray();
		}
	}
}