package com.marketcruiser.admin.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderReportRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin123", authorities = {"Admin"})
    public void testGetReportDataLast7Days() throws Exception {
        String requestURL = "/reports/sales_by_date/last_7_days";

        mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin123", authorities = {"Admin"})
    public void testGetReportDataLast6Months() throws Exception {
        String requestURL = "/reports/sales_by_date/last_6_months";

        mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
    }
}
