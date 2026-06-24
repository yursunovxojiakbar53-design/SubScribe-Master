package com.example.d.report.generator;

import com.example.d.report.dto.SubscriptionReportRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvGenerator {

    private static final String HEADER =
            "Obuna nomi,Oylik narx,Valyuta,Asosiy valyutadagi ekvivalenti,Asosiy valyuta," +
                    "Oxirgi 1 oylik haqiqiy xarajat,Oxirgi 1 yillik haqiqiy xarajat";

    public byte[] generate(List<SubscriptionReportRow> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append("\n");

        for (SubscriptionReportRow row : rows) {
            sb.append(escape(row.serviceName())).append(",")
                    .append(row.monthlyPrice()).append(",")
                    .append(row.currency()).append(",")
                    .append(row.equivalentInBaseCurrency()).append(",")
                    .append(row.baseCurrency()).append(",")
                    .append(row.monthlyTotalInBaseCurrency()).append(",")
                    .append(row.yearlyTotalInBaseCurrency())
                    .append("\n");
        }

        BigDecimal grandTotal = rows.stream()
                .map(SubscriptionReportRow::yearlyTotalInBaseCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sb.append("JAMI (oxirgi 1 yillik haqiqiy),,,,,,").append(grandTotal).append("\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}