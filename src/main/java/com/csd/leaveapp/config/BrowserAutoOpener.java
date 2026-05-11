package com.csd.leaveapp.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class BrowserAutoOpener {
    private static final AtomicBoolean OPENED = new AtomicBoolean(false);
    private final Environment env;

    public BrowserAutoOpener(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        boolean enabled = Boolean.parseBoolean(env.getProperty("app.auto-open-browser", "true"));
        if (!enabled) return;
        if (!OPENED.compareAndSet(false, true)) return;

        try {
            if (!Desktop.isDesktopSupported()) return;
            if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) return;

            String port = env.getProperty("local.server.port", env.getProperty("server.port", "8080"));
            String contextPath = env.getProperty("server.servlet.context-path", "");
            if (contextPath == null) contextPath = "";
            if (!contextPath.isEmpty() && contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }

            String loginUrl = "http://localhost:" + port + contextPath + "/login";
            Desktop.getDesktop().browse(new URI(loginUrl));
        } catch (URISyntaxException | IOException | UnsupportedOperationException | SecurityException ignored) {
            // Fallback for environments where Desktop browse is unavailable.
            try {
                String port = env.getProperty("local.server.port", env.getProperty("server.port", "8080"));
                String contextPath = env.getProperty("server.servlet.context-path", "");
                if (contextPath == null) contextPath = "";
                if (!contextPath.isEmpty() && contextPath.endsWith("/")) {
                    contextPath = contextPath.substring(0, contextPath.length() - 1);
                }
                String loginUrl = "http://localhost:" + port + contextPath + "/login";
                String os = System.getProperty("os.name", "").toLowerCase();
                if (os.contains("win")) {
                    new ProcessBuilder("cmd", "/c", "start", "", loginUrl).start();
                }
            } catch (IOException | SecurityException ignoredAgain) {
                // Ignore fallback failures too.
            }
        }
    }
}

