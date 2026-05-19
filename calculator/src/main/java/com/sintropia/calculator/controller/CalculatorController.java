package com.sintropia.calculator.controller;

import com.sintropia.calculator.dto.CalculoRequestDTO;
import com.sintropia.calculator.dto.CalculoResponseDTO;
import com.sintropia.calculator.service.CalculadoraService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@RestController
@RequestMapping("/api/calculator")
@CrossOrigin(origins = "*")
public class CalculatorController {

    private final CalculadoraService service;

    public CalculatorController(CalculadoraService service) {
        this.service = service;
    }

    @PostMapping("/calcular")
    public ResponseEntity<CalculoResponseDTO> calcular(
            @RequestBody CalculoRequestDTO request) {

        CalculoResponseDTO response = service.calcular(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestBody CalculoRequestDTO request) {

        CalculoResponseDTO result = service.calcular(request);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("Emissao Fisica,Emissao Digital,Diferenca,Percentual Reducao");
        writer.println(result.getEmissaoCartaoFisico() + "," +
                       result.getEmissaoCartaoDigital() + "," +
                       result.getDiferenca() + "," +
                       result.getPercentualReducao());

        writer.flush();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=resultado_co2.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(out.toByteArray());
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestBody CalculoRequestDTO request) throws Exception {

        CalculoResponseDTO result = service.calcular(request);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Resultado Cálculo CO2"));
        document.add(new Paragraph("----------------------------------------"));
        document.add(new Paragraph("Emissão Física: " + result.getEmissaoCartaoFisico() + " kg"));
        document.add(new Paragraph("Emissão Digital: " + result.getEmissaoCartaoDigital() + " kg"));
        document.add(new Paragraph("Diferença: " + result.getDiferenca() + " kg"));
        document.add(new Paragraph("Redução (%): " + result.getPercentualReducao() + " %"));

        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=resultado_co2.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }
}