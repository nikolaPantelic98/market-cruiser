package com.marketcruiser.admin.report;

import java.util.Date;
import java.util.List;

public interface OrderReportService {

    List<ReportItem> getReportDataLast7Days();
    List<ReportItem> getReportDataLast28Days();
    List<ReportItem> getReportDataLast6Months();
    List<ReportItem> getReportDataLast1Year();
    List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime);
}
