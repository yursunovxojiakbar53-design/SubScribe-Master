package com.example.d.report.controller;


import com.example.d.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadExcel(Authentication authentication) {
        byte[] content = reportService.generateExcelReport(authentication);
        return buildFileResponse(content, "yillik-hisobot.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadCsv(Authentication authentication) {
        byte[] content = reportService.generateCsvReport(authentication);
        return buildFileResponse(content, "yillik-hisobot.csv", "text/csv");
    }

    private ResponseEntity<byte[]> buildFileResponse(byte[] content, String filename, String contentType) {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(filename)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(disposition);
        headers.setContentType(MediaType.parseMediaType(contentType));

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }


}
