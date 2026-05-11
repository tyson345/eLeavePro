package com.csd.leaveapp.config;

import com.csd.leaveapp.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Public routes / static assets
        if (uri.equals("/") ||
                uri.startsWith("/login") ||
                uri.startsWith("/signup") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/favicon")) {
            return true;
        }

        // For authenticated areas, always prevent browser caching.
        if (uri.startsWith("/hod/") || uri.startsWith("/student/")) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        Object role = session.getAttribute("role");
        if (role == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (uri.startsWith("/hod/")) {
            if (!Role.HOD.name().equals(role)) {
                response.sendRedirect("/login");
                return false;
            }
        } else if (uri.startsWith("/student/")) {
            if (!Role.STUDENT.name().equals(role)) {
                response.sendRedirect("/login");
                return false;
            }
        }

        return true;
    }
}

