package com.marketcruiser.admin.user.export;

import com.marketcruiser.admin.AbstractExporter;
import com.marketcruiser.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * The UserCsvExporter class exports a list of users to a CSV file and sends it to the client.
 */
public class UserCsvExporter extends AbstractExporter {

    /**
     * Exports a list of users to a CSV file and sends it to the client.
     *
     * @param listUsers the list of users to export
     * @param response the HTTP servlet response object to send the CSV file
     * @throws IOException if there is an error writing to the response output stream
     */
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "users_");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"User ID", "Email Address", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping = {"userId", "emailAddress", "firstName", "lastName", "roles", "enabled"};

        csvWriter.writeHeader(csvHeader);

        for (User user : listUsers) {
            csvWriter.write(user, fieldMapping);
        }

        csvWriter.close();
    }
}
