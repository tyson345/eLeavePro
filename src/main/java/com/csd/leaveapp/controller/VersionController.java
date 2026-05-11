package com.csd.leaveapp.controller;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class VersionController {
    private final Environment env;

    public VersionController(Environment env) {
        this.env = env;
    }

    /**
     * Lightweight endpoint to confirm what Render is currently running.
     * Render provides RENDER_GIT_COMMIT and RENDER_SERVICE_NAME env vars.
     */
    @GetMapping("/__version")
    @ResponseBody
    public Map<String, Object> version() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("now", Instant.now().toString());
        out.put("appName", env.getProperty("spring.application.name", "eLeavePro"));
        out.put("renderService", env.getProperty("RENDER_SERVICE_NAME", ""));
        out.put("renderCommit", env.getProperty("RENDER_GIT_COMMIT", ""));
        out.put("renderDeployId", env.getProperty("RENDER_DEPLOY_ID", ""));
        out.put("port", env.getProperty("local.server.port", env.getProperty("server.port", "")));
        return out;
    }
}

