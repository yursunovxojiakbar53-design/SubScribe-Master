package com.example.d.report.service;

import org.springframework.security.core.Authentication;


public interface ReportService {

    byte[] generateExcelReport(Authentication authentication);
    byte[] generateCsvReport(Authentication authentication);
}