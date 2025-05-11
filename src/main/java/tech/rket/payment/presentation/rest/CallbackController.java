package tech.rket.payment.presentation.rest;

import tech.rket.payment.domain.shared.IPG;
import tech.rket.payment.infrastructure.dto.Redirect;
import tech.rket.payment.service.transaction.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Controller
@RequiredArgsConstructor
@RequestMapping("payment/callback")
@EnableWebMvc
public class CallbackController {
    private final TransactionService transactionService;

    @PostMapping(
            value = "melli",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ModelAndView melliCallBack(HttpServletResponse response, @RequestBody MultiValueMap<String, String> paramMap) {
        Redirect attemptRedirect = transactionService.callBack(IPG.SADAD, paramMap.toSingleValueMap());
        return RedirectUtil.redirect(response, attemptRedirect);
    }

}
