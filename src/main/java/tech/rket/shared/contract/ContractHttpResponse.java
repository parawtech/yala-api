package tech.rket.shared.contract;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Response;

import java.io.IOException;

public class ContractHttpResponse extends Response {
    public void apply(HttpServletResponse response) throws IOException {
        response.setStatus(this.getStatus());
        if (this.getCookies() != null) {
            for (Cookie cookie : this.getCookies()) {
                response.addCookie(cookie);
            }
        }

        if (this.getHeaderNames() != null) {
            for (String headerName : this.getHeaderNames()) {
                this.getHeaders(headerName)
                        .forEach(e -> response.addHeader(headerName, e));
            }
        }

        if (this.getContentType() != null) {
            response.setContentType(this.getContentType());
        }
        if (this.getCharacterEncoding() != null) {
            response.setCharacterEncoding(this.getCharacterEncoding());
        }

        if (this.getResponse().isCommitted()) {
            response.flushBuffer();
        } else if (this.getBufferSize() > 0) {
            response.setBufferSize(this.getBufferSize());
        }

        if (this.getLocale() != null) {
            response.setLocale(this.getLocale());
        }

        if (this.getTrailerFields() != null) {
            response.setTrailerFields(this.getTrailerFields());
        }
    }
}
