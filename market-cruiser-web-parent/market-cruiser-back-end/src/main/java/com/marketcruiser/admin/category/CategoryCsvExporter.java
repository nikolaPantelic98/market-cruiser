package com.marketcruiser.admin.category;

import com.marketcruiser.admin.AbstractExporter;
import com.marketcruiser.common.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * The CategoryCsvExporter class exports a list of categories to a CSV file and sends it to the client.
 */
public class CategoryCsvExporter extends AbstractExporter {

    /**
     * Exports a list of categories to a CSV file and sends it to the client.
     *
     * @param listCategories the list of categories to export
     * @param response the HTTP servlet response object to send the CSV file
     * @throws IOException if there is an error writing to the response output stream
     */
    public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Category ID", "Category Name"};
        String[] fieldMapping = {"categoryId", "name"};

        csvWriter.writeHeader(csvHeader);

        for (Category category : listCategories) {
            category.setName(category.getName().replace("--", "  "));
            csvWriter.write(category, fieldMapping);
        }

        csvWriter.close();
    }
}
