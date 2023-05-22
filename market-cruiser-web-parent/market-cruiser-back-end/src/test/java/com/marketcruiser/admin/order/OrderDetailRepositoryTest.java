package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.order.OrderDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;


    @Test
    public void testFindWithCategoryAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date startTime = dateFormatter.parse("01-04-2023 00:00:00");
        Date endTime = dateFormatter.parse("30-04-2023 23:59:59");

        List<OrderDetail> listOrderDetails = orderDetailRepository.findWithCategoryAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails.size()).isGreaterThan(0);

        for (OrderDetail detail : listOrderDetails) {
            System.out.printf("%s | %d | %.2f | %.2f | %.2f \n",
                    detail.getProduct().getCategory().getName(),
                    detail.getQuantity(), detail.getProductCost(),
                    detail.getShippingCost(), detail.getSubtotal());
        }
    }

    @Test
    public void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date startTime = dateFormatter.parse("01-04-2023 00:00:00");
        Date endTime = dateFormatter.parse("30-04-2023 23:59:59");

        List<OrderDetail> listOrderDetails = orderDetailRepository.findWithProductAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails.size()).isGreaterThan(0);

        for (OrderDetail detail : listOrderDetails) {
            System.out.printf("%s | %d | %.2f | %.2f | %.2f \n",
                    detail.getProduct().getName(),
                    detail.getQuantity(), detail.getProductCost(),
                    detail.getShippingCost(), detail.getSubtotal());
        }
    }
}
