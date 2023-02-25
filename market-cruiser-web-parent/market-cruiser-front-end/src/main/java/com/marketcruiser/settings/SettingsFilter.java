package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Component
public class SettingsFilter implements Filter {

    private final SettingsServiceImpl settingsService;

    @Autowired
    public SettingsFilter(SettingsServiceImpl settingsService) {
        this.settingsService = settingsService;
    }

    // filters incoming request, sets general settings as request attributes
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

        chain.doFilter(request, response);
    }
}
