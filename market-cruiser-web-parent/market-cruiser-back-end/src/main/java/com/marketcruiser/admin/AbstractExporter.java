package com.marketcruiser.admin;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class serves as a base class for exporting data to various formats.
 */
public class AbstractExporter {

    /**
     * Sets the appropriate response headers on the given HttpServletResponse object, allowing the user to download a file.
     *
     * @param response    the HttpServletResponse object to set headers on
     * @param contentType the MIME type of the file to be downloaded
     * @param extension   the file extension to be used for the downloaded file
     * @param prefix      the prefix to be used for the downloaded file name
     * @throws IOException if an I/O error occurs while setting the response headers
     */
    public void setResponseHeader(HttpServletResponse response, String contentType, String extension, String prefix) throws IOException {
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    String timestamp = dateFormatter.format(new Date());
    String fileName = prefix + timestamp + extension;

    response.setContentType(contentType);

    String headerKey = "Content-Disposition";
    String headerValue = "attachment; filename=" + fileName;
    response.setHeader(headerKey, headerValue);
    }
}
