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

@Service
public class OrderReportServiceImpl implements OrderReportService{

    private final OrderRepository orderRepository;
    private DateFormat dateFormatter;

    @Autowired
    public OrderReportServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    public List<ReportItem> getReportDataLast7Days() {
        System.out.println("getReportDataLast7Days...");

        return getReportDataLastXDays(7);
    }

    private List<ReportItem> getReportDataLastXDays(int days) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(days - 1));
        Date startTime = calendar.getTime();

        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByDateRange(startTime, endTime);
    }

    private List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime) {
        List<Order> listOrders = orderRepository.findByOrderTimeBetween(startTime, endTime);

        printRawData(listOrders);

        List<ReportItem> listReportItems = createReportData(startTime, endTime);

        calculateSalesForReportData(listOrders, listReportItems);
        printReportData(listReportItems);

        return listReportItems;
    }

    private void printRawData(List<Order> listOrders) {
        listOrders.forEach(order -> {
            System.out.printf("%-3d | %s | %10.2f | %10.2f \n",
                    order.getOrderId(), order.getOrderTime(), order.getTotal(), order.getProductCost());
        });
    }

    private List<ReportItem> createReportData(Date startTime, Date endTime) {
        List<ReportItem> listReportItems = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        Date currentDate = startDate.getTime();
        String dateString = dateFormatter.format(currentDate);

        listReportItems.add(new ReportItem(dateString));

        do {
            startDate.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = startDate.getTime();
            dateString = dateFormatter.format(currentDate);

            listReportItems.add(new ReportItem(dateString));

        } while (startDate.before(endDate));

        return listReportItems;
    }

    private void printReportData(List<ReportItem> listReportItems) {
        listReportItems.forEach(reportItem -> {
            System.out.printf("%s, %10.2f, %10.2f, %d \n",
                    reportItem.getIdentifier(), reportItem.getGrossSales(), reportItem.getNetSales(), reportItem.getOrdersCount());
        });
    }

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
}
