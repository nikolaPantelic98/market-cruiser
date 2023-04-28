package com.marketcruiser.admin.user.export;

import com.marketcruiser.admin.AbstractExporter;
import com.marketcruiser.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * An implementation of an Excel exporter for a list of users.
 */
public class UserExcelExporter extends AbstractExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;


    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }


    /**
     * Private helper method to write the header row to the sheet in the workbook.
     */
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "User ID", cellStyle);
        createCell(row, 1, "Email Address", cellStyle);
        createCell(row, 2, "First Name", cellStyle);
        createCell(row, 3, "Last Name", cellStyle);
        createCell(row, 4, "Roles", cellStyle);
        createCell(row, 5, "Enabled", cellStyle);
    }

    /**
     * Private helper method to create a new cell in the given row at the specified column index and sets its value and style.
     *
     * @param row the row where the cell will be created
     * @param columnIndex the index of the column where the cell will be created
     * @param value the value to be set in the cell
     * @param style the style to be applied to the cell
     */
    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }

    /**
     * Exports the given list of users to an Excel file.
     *
     * @param listUsers the list of users to be exported
     * @param response the HTTP response to which the Excel file will be written
     * @throws IOException if an error occurs while writing the file
     */
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx", "users_");

        writeHeaderLine();
        writeDataLines(listUsers);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    /**
     * Private helper method to write the data rows for the given list of users to the sheet in the workbook.
     *
     * @param listUsers the list of users to be written
     */
    private void writeDataLines(List<User> listUsers) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (User user : listUsers) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, user.getUserId(), cellStyle);
            createCell(row, columnIndex++, user.getEmailAddress(), cellStyle);
            createCell(row, columnIndex++, user.getFirstName(), cellStyle);
            createCell(row, columnIndex++, user.getLastName(), cellStyle);
            createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
            createCell(row, columnIndex, user.isEnabled(), cellStyle);
        }
    }
}
