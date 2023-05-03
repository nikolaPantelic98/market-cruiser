package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Constants;
import com.marketcruiser.common.entity.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * A filter that intercepts incoming requests and sets general settings as request attributes.
 * Implements the {@link Filter} interface.
 */
@Component
public class SettingsFilter implements Filter {

    private final SettingsServiceImpl settingsService;

    @Autowired
    public SettingsFilter(SettingsServiceImpl settingsService) {
        this.settingsService = settingsService;
    }


    /**
     * Filters incoming requests and sets general settings as request attributes.
     *
     * @param request  The ServletRequest object representing the incoming request.
     * @param response The ServletResponse object representing the response to send back to the client.
     * @param chain    The FilterChain object representing the chain of filters to execute.
     * @throws IOException      If an I/O error occurs while processing the request.
     * @throws ServletException If an error occurs while processing the request.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();

        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        List<Settings> generalSettings = settingsService.getGeneralSettings();

        generalSettings.forEach(settings -> {
            request.setAttribute(settings.getKey(), settings.getValue());
        });

        request.setAttribute("S3_BASE_URI", Constants.S3_BASE_URI);

        chain.doFilter(request, response);
    }
}