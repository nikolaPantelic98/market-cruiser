package com.marketcruiser.admin.report;

import java.util.List;

public interface OrderReportService {

    List<ReportItem> getReportDataLast7Days();
}
