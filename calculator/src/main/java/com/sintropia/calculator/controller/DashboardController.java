package com.sintropia.calculator.controller;

import java.util.List;

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
import com.sintropia.calculator.dto.response.DashboardResponseDTO;
import com.sintropia.calculator.service.DashboardExportService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("/api/dashboards")
public class DashboardController {

 private final UserService userService;
 private final DashboardExportService exportService;

 public DashboardController(UserService userService, DashboardExportService exportService) {
     this.userService = userService;
     this.exportService = exportService;
}
 
 @GetMapping
 public ResponseEntity<List<DashboardResponseDTO>> listDashboards() {
     List<DashboardResponseDTO> dashboards = List.of(
         new DashboardResponseDTO("emissao-prevista", "Emissão Prevista", "/"),
         new DashboardResponseDTO("historico-metas", "Histórico e Metas", "/history")
     );
     return ResponseEntity.ok(dashboards);
} 
 

 @GetMapping("/{id}/export")
 public ResponseEntity<byte[]> exportDashboard(
         @PathVariable String id,
         @RequestParam String format,
         @AuthenticationPrincipal String email) {

     try {
         UserDTO user = userService.findByEmail(email);
         
         byte[] fileBytes = exportService.export(id, format, user);
         
         String filename = id + "." + format;
         MediaType mediaType = "pdf".equalsIgnoreCase(format) ? 
             MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM;

         HttpHeaders headers = new HttpHeaders();
         headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

         return ResponseEntity.ok()
             .headers(headers)
             .contentType(mediaType)
             .body(fileBytes);
             
     } catch (IllegalArgumentException e) {
         return ResponseEntity.badRequest().build();
     } catch (Exception e) {
         return ResponseEntity.internalServerError().build();
     }
 }
}