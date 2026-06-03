package com.sintropia.calculator.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.sintropia.calculator.dto.request.CalculoRequestDTO;
import com.sintropia.calculator.dto.response.CalculoResponseDTO;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.CalculatorService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("/api/calculator")
@CrossOrigin(origins = "*")
public class CalculatorController {

    private final CalculatorService calculatorService;
    private final UserService userService;
    
    public CalculatorController(CalculatorService calculatorService,UserService userService) {
        this.calculatorService = calculatorService;
        this.userService = userService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculate(@AuthenticationPrincipal String email) throws Exception{
    	User user = userService.findByEmail(email);
    	return ResponseEntity.ok(calculatorService.calculate(user));
    }
    
    @PostMapping("/calcular")
    public ResponseEntity<CalculoResponseDTO> calcular(@RequestBody CalculoRequestDTO request) {

        CalculoResponseDTO response = calculatorService.calcular(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestBody CalculoRequestDTO request) {

        CalculoResponseDTO result = calculatorService.calcular(request);

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

        CalculoResponseDTO result = calculatorService.calcular(request);

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