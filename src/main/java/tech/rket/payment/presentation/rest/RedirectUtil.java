package tech.rket.payment.presentation.rest;

import tech.rket.payment.infrastructure.dto.Redirect;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

public class RedirectUtil {
    public static ModelAndView redirect(HttpServletResponse response, Redirect redirect) {
        if (!redirect.isForm()) {
            response.setStatus(302);
            try {
                response.sendRedirect(redirect.getUrl());
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            ModelAndView mnv = new ModelAndView("callback");
            mnv.addObject("form", redirect.getForm());
            mnv.addObject("url", redirect.getUrl());
            return mnv;
        }
    }
}
