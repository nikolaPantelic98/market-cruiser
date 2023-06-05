package com.marketcruiser.admin.report;

import com.marketcruiser.admin.order.OrderRepository;
import com.marketcruiser.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The OrderReportServiceImpl class is a service implementation that provides methods to generate order reports based on
 * different time periods. It retrieves order data from the {@link OrderRepository} and performs calculations to generate the
 * report items.
 */
@Service
public class OrderReportServiceImpl implements OrderReportService {

    private final OrderRepository orderRepository;
    private DateFormat dateFormatter;

    @Autowired
    public OrderReportServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    /**
     * Retrieves the report data for the last 7 days.
     *
     * @return a list of {@link ReportItem} objects representing the report data
     */
    @Override
    public List<ReportItem> getReportDataLast7Days() {
        return getReportDataLastXDays(7);
    }

    /**
     * Retrieves the report data for the last 28 days.
     *
     * @return a list of {@link ReportItem} objects representing the report data
     */
    @Override
    public List<ReportItem> getReportDataLast28Days() {
        return getReportDataLastXDays(28);
    }

    /**
     * Retrieves the report data for the last X days.
     *
     * @param days the number of days to consider for the report
     * @return a list of {@link ReportItem} objects representing the report data
     */
    private List<ReportItem> getReportDataLastXDays(int days) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(days - 1));
        Date startTime = calendar.getTime();

        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByDateRange(startTime, endTime, "days");
    }

    /**
     * Retrieves the report data for the given date range.
     *
     * @param startTime the start time of the date range
     * @param endTime   the end time of the date range
     * @return a list of {@link ReportItem} objects representing the report data
     */
    @Override
    public List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByDateRange(startTime, endTime, "days");
    }

    /**
     * Retrieves the report data for the given date range and period.
     *
     * @param startTime the start time of the date range
     * @param endTime   the end time of the date range
     * @param period    the period of the report data ("days" or "months")
     * @return a list of {@link ReportItem} objects representing the report data
     */
    private List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime, String period) {
        List<Order> listOrders = orderRepository.findByOrderTimeBetween(startTime, endTime);

        printRawData(listOrders);

        List<ReportItem> listReportItems = createReportData(startTime, endTime, period);

        calculateSalesForReportData(listOrders, listReportItems);
        printReportData(listReportItems);

        return listReportItems;
    }

    /**
     * Prints the raw order data to the console.
     *
     * @param listOrders the list of Order objects to print
     */
    private void printRawData(List<Order> listOrders) {
        listOrders.forEach(order -> {
            System.out.printf("%-3d | %s | %10.2f | %10.2f \n",
                    order.getOrderId(), order.getOrderTime(), order.getTotal(), order.getProductCost());
        });
    }

    /**
     * Creates the report data structure for the given date range and period.
     *
     * @param startTime the start time of the date range
     * @param endTime   the end time of the date range
     * @param period    the period of the report data ("days" or "months")
     * @return a list of {@link ReportItem} objects representing the report data structure
     */
    private List<ReportItem> createReportData(Date startTime, Date endTime, String period) {
        List<ReportItem> listReportItems = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        Date currentDate = startDate.getTime();
        String dateString = dateFormatter.format(currentDate);

        listReportItems.add(new ReportItem(dateString));

        do {
            if (period.equals("days")) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            } else if (period.equals("months")) {
                startDate.add(Calendar.MONTH, 1);
            }

            currentDate = startDate.getTime();
            dateString = dateFormatter.format(currentDate);

            listReportItems.add(new ReportItem(dateString));

        } while (startDate.before(endDate));

        return listReportItems;
    }

    /**
     * Prints the report data to the console.
     *
     * @param listReportItems the list of ReportItem objects to print
     */
    private void printReportData(List<ReportItem> listReportItems) {
        listReportItems.forEach(reportItem -> {
            System.out.printf("%s, %10.2f, %10.2f, %d \n",
                    reportItem.getIdentifier(), reportItem.getGrossSales(), reportItem.getNetSales(), reportItem.getOrdersCount());
        });
    }

    /**
     * Calculates the sales data for the report items based on the order data.
     *
     * @param listOrders       the list of {@link Order} objects to process
     * @param listReportItems  the list of {@link ReportItem} objects to update with sales data
     */
    private void calculateSalesForReportData(List<Order> listOrders, List<ReportItem> listReportItems) {
        for (Order order : listOrders) {
            String orderDateString = dateFormatter.format(order.getOrderTime());

            ReportItem reportItem = new ReportItem(orderDateString);

            int itemIndex = listReportItems.indexOf(reportItem);

            if (itemIndex >= 0) {
                reportItem = listReportItems.get(itemIndex);

                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseOrderCount();
            }
        }
    }

    /**
     * Retrieves the report data for the last 6 months.
     *
     * @return a list of {@link ReportItem} objects representing the report data
     */
    @Override
    public List<ReportItem> getReportDataLast6Months() {
        return getReportDataLastXMonths(6);
    }

    /**
     * Retrieves the report data for the last 1 year.
     *
     * @return a list of {@link ReportItem} objects representing the report data
     */
    @Override
    public List<ReportItem> getReportDataLast1Year() {
        return getReportDataLastXMonths(12);
    }

    /**
     * Retrieves the report data for the last X months.
     *
     * @param months the number of months to consider for the report
     * @return a list of {@link ReportItem} objects representing the report data
     */
    private List<ReportItem> getReportDataLastXMonths(int months) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -(months - 1));
        Date startTime = calendar.getTime();

        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);

        dateFormatter = new SimpleDateFormat("yyyy-MM");
        return getReportDataByDateRange(startTime, endTime, "months");
    }
}