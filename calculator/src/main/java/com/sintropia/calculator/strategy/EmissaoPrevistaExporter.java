package com.sintropia.calculator.strategy;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.service.CalculatorService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component 
public class EmissaoPrevistaExporter implements DashboardExporter {

    private final CalculatorService calculatorService;

    public EmissaoPrevistaExporter(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @Override
    public String getDashboardId() {
        return "emissao-prevista";
    }

    @Override
    public byte[] exportExcel(UserDTO user) throws Exception {

        CalculationResponseDTO data = calculatorService.calculate(user);

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

    @Override
    public byte[] exportPdf(UserDTO user) throws Exception {

        CalculationResponseDTO data = calculatorService.calculate(user);

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