package tech.rket.auth.application.user.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

public record UserInfo(@JsonInclude(JsonInclude.Include.NON_NULL) @JsonFormat(shape = JsonFormat.Shape.STRING) Long id,
                       @JsonInclude(JsonInclude.Include.NON_NULL) String mobile,
                       @JsonInclude(JsonInclude.Include.NON_NULL) String email,
                       @JsonInclude(JsonInclude.Include.NON_NULL) String name) {
}
