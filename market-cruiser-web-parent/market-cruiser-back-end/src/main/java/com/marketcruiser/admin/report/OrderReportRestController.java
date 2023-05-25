package com.marketcruiser.admin.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderReportRestController {

    private final OrderReportServiceImpl orderReportService;

    @Autowired
    public OrderReportRestController(OrderReportServiceImpl orderReportService) {
        this.orderReportService = orderReportService;
    }


    @GetMapping("/reports/sales_by_date/{period}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable String period) {
        System.out.println("Report period: " + period);
        return orderReportService.getReportDataLast7Days();
    }
}
