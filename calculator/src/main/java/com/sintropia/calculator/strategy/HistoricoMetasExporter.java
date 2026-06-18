package com.sintropia.calculator.strategy;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.service.CalculatorService;
import com.sintropia.calculator.service.GoalsService;
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
public class HistoricoMetasExporter implements DashboardExporter {

    private final CalculatorService calculatorService;
    private final GoalsService goalsService;

    public HistoricoMetasExporter(CalculatorService calculatorService, GoalsService goalsService) {
        this.calculatorService = calculatorService;
        this.goalsService = goalsService;
    }

    @Override
    public String getDashboardId() {
        return "historico-metas";
    }

    @Override
    public byte[] exportExcel(UserDTO user) throws Exception {
        double emission = 0.0;
        if (user.digitalCardStaffCount() != null) {
            emission = calculatorService.calculateAnualPhysicEmission(user.digitalCardStaffCount(), user.address());
        }
        
        var goals = goalsService.calculateGoals(user);
        var records = user.monthlyRecords();

        double migrationIndex = (user.digitalCardStaffCount() != null && user.staffCount() > 0)
                ? (user.digitalCardStaffCount() * 100.0 / user.staffCount()) : 0.0;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet summarySheet = workbook.createSheet("Resumo e Metas");
            
            Row row0 = summarySheet.createRow(0);
            row0.createCell(0).setCellValue("Indicador Geral");
            row0.createCell(1).setCellValue("Valor");

            Row row1 = summarySheet.createRow(1);
            row1.createCell(0).setCellValue("Índice de Migração Digital");
            row1.createCell(1).setCellValue(String.format("%.0f%%", migrationIndex));

            Row row2 = summarySheet.createRow(2);
            row2.createCell(0).setCellValue("Funcionários Digitais");
            row2.createCell(1).setCellValue(user.digitalCardStaffCount() != null ? String.valueOf(user.digitalCardStaffCount()) : "Dados Pendentes");

            Row row3 = summarySheet.createRow(3);
            row3.createCell(0).setCellValue("Total de Funcionários");
            row3.createCell(1).setCellValue(String.valueOf(user.staffCount()));

            Row row4 = summarySheet.createRow(4);
            row4.createCell(0).setCellValue("CO2 Evitado Acumulado (kg/ano)");
            row4.createCell(1).setCellValue(String.format("%.1f kg", emission));

            if (goals != null) {
                Row row5 = summarySheet.createRow(5);
                row5.createCell(0).setCellValue("Progresso da Meta Atual");
                row5.createCell(1).setCellValue(String.format("%.0f%%", goals.currentPercentage()));

                Row row6 = summarySheet.createRow(6);
                row6.createCell(0).setCellValue("Funcionários Restantes para Meta");
                row6.createCell(1).setCellValue(String.valueOf(goals.remainingStaff()));
            }

            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            Sheet recordsSheet = workbook.createSheet("Histórico Mensal");
            Row headerRow = recordsSheet.createRow(0);
            headerRow.createCell(0).setCellValue("Data do Registro");
            headerRow.createCell(1).setCellValue("Cartão Digital (Colaboradores)");
            headerRow.createCell(2).setCellValue("Cartão Físico (Colaboradores)");
            headerRow.createCell(3).setCellValue("Total (Colaboradores)");

            if (records != null) {
                int rowNum = 1;
                for (var r : records) {
                    Row row = recordsSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(r.recordDate() != null ? r.recordDate().toString() : "");
                    row.createCell(1).setCellValue(r.digitalStaffCount() != null ? r.digitalStaffCount() : 0);
                    row.createCell(2).setCellValue((r.staffCount() != null && r.digitalStaffCount() != null) ? (r.staffCount() - r.digitalStaffCount()) : 0);
                    row.createCell(3).setCellValue(r.staffCount() != null ? r.staffCount() : 0);
                }
            }

            for (int i = 0; i < 4; i++) {
                recordsSheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public byte[] exportPdf(UserDTO user) throws Exception {
        double emission = 0.0;
        if (user.digitalCardStaffCount() != null) {
            emission = calculatorService.calculateAnualPhysicEmission(user.digitalCardStaffCount(), user.address());
        }
        var goals = goalsService.calculateGoals(user);
        var records = user.monthlyRecords();

        double migrationIndex = (user.digitalCardStaffCount() != null && user.staffCount() > 0)
                ? (user.digitalCardStaffCount() * 100.0 / user.staffCount()) : 0.0;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float rowHeight = 20;

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("Relatorio - Historico e Metas");
                content.endText();
                y -= 35;

                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("1. Indicadores Gerais e Evolucao");
                content.endText();
                y -= 20;

                content.setFont(PDType1Font.HELVETICA, 10);
                String[][] summaryRows = {
                    {"Indice de Migracao Digital:", String.format("%.0f%%", migrationIndex)},
                    {"Funcionarios Digitais:", user.digitalCardStaffCount() != null ? String.valueOf(user.digitalCardStaffCount()) : "Pendente"},
                    {"Total de Funcionarios:", String.valueOf(user.staffCount())},
                    {"CO2 Evitado Acumulado:", String.format("%.1f kg", emission)},
                    {"Situacao Atual da Meta:", goals != null ? String.format("%.0f%%", goals.currentPercentage()) : "N/A"},
                    {"Funcionarios para Meta:", goals != null ? String.valueOf(goals.remainingStaff()) : "N/A"}
                };

                for (String[] row : summaryRows) {
                    content.beginText();
                    content.newLineAtOffset(margin, y);
                    content.showText(row[0] + " " + row[1]);
                    content.endText();
                    y -= rowHeight;
                }

                y -= 15;

                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("2. Historico de Registros Mensais");
                content.endText();
                y -= 20;

                content.setFont(PDType1Font.HELVETICA_BOLD, 10);
                float col1 = margin;
                float col2 = margin + 110;
                float col3 = margin + 230;
                float col4 = margin + 350;

                content.beginText(); content.newLineAtOffset(col1, y); content.showText("Data"); content.endText();
                content.beginText(); content.newLineAtOffset(col2, y); content.showText("Cartao Digital"); content.endText();
                content.beginText(); content.newLineAtOffset(col3, y); content.showText("Cartao Fisico"); content.endText();
                content.beginText(); content.newLineAtOffset(col4, y); content.showText("Total"); content.endText();
                y -= rowHeight;

                content.setFont(PDType1Font.HELVETICA, 10);
                if (records != null) {
                    for (var r : records) {
                        if (y < margin) break; 

                        String dataFormatada = r.recordDate() != null ? r.recordDate().toString() : "";
                        String digitalCount = r.digitalStaffCount() != null ? String.valueOf(r.digitalStaffCount()) : "0";
                        
                        long totalStaff = r.staffCount() != null ? r.staffCount() : 0;
                        long digitalStaff = r.digitalStaffCount() != null ? r.digitalStaffCount() : 0;
                        String fisicoCount = String.valueOf(totalStaff - digitalStaff);
                        String totalCount = String.valueOf(totalStaff);

                        content.beginText(); content.newLineAtOffset(col1, y); content.showText(dataFormatada); content.endText();
                        content.beginText(); content.newLineAtOffset(col2, y); content.showText(digitalCount); content.endText();
                        content.beginText(); content.newLineAtOffset(col3, y); content.showText(fisicoCount); content.endText();
                        content.beginText(); content.newLineAtOffset(col4, y); content.showText(totalCount); content.endText();
                        y -= rowHeight;
                    }
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}