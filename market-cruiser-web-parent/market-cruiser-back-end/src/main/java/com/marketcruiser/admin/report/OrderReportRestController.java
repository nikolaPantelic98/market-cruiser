package com.marketcruiser.admin.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The OrderReportRestController class is a REST controller responsible for handling requests related to order reports.
 * It interacts with the {@link OrderReportServiceImpl} to retrieve sales report data based on different date periods.
 */
@RestController
public class OrderReportRestController {

    private final OrderReportServiceImpl orderReportService;

    @Autowired
    public OrderReportRestController(OrderReportServiceImpl orderReportService) {
        this.orderReportService = orderReportService;
    }


    /**
     * Retrieves the sales report data for the specified date period.
     *
     * @param period the period for which to retrieve the sales report data
     * @return a list of ReportItem objects representing the sales report data
     */
    @GetMapping("/reports/sales_by_date/{period}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable String period) {

        switch (period) {
            case "last_7_days":
                return orderReportService.getReportDataLast7Days();

            case "last_28_days":
                return orderReportService.getReportDataLast28Days();

            case "last_6_months":
                return orderReportService.getReportDataLast6Months();

            case "last_1_year":
                return orderReportService.getReportDataLast1Year();

            default:
                return orderReportService.getReportDataLast7Days();
        }
    }

    /**
     * Retrieves the sales report data for the specified date range.
     *
     * @param startDate the start date of the date range in the format "yyyy-MM-dd"
     * @param endDate   the end date of the date range in the format "yyyy-MM-dd"
     * @return a list of {@link ReportItem} objects representing the sales report data
     * @throws ParseException if the start date or end date is not in the correct format
     */
    @GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable String startDate, @PathVariable String endDate) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);

        return orderReportService.getReportDataByDateRange(startTime, endTime);
    }
}