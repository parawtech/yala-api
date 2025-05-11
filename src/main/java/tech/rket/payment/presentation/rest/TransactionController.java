package tech.rket.payment.presentation.rest;

import tech.rket.payment.domain.transaction.Transaction;
import tech.rket.payment.infrastructure.dto.transaction.TransactionDTO;
import tech.rket.payment.service.transaction.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
@RequestMapping("payment/owners/{ownerIdentifier}")
public class TransactionController {
    private final TransactionService service;

    @PostMapping("transactions")
    public Object createTransaction(
            @Valid @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\-]{1,34}[a-zA-Z0-9]$") @NotEmpty @PathVariable("ownerIdentifier") String ownerIdentifier,
            @Valid @RequestBody TransactionDTO transactionDTO,
            @RequestParam(value = "settlement", defaultValue = "false") boolean settlement,
            HttpServletResponse response
    ) {
        Transaction dto = service.create(ownerIdentifier, transactionDTO);
        if (settlement) {
            var settlementResult = service.settlement(ownerIdentifier, dto.getReference());
            return RedirectUtil.redirect(response, settlementResult);
        } else {
            return ResponseEntity.ok(service.map(dto));
        }
    }

    @PostMapping("transactions/{transactionReference}/settlement")
    public ModelAndView settlement(
            @Valid @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\-]{1,34}[a-zA-Z0-9]$") @PathVariable("ownerIdentifier") String ownerIdentifier,
            @Valid @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\-]{1,34}[a-zA-Z0-9]$") @PathVariable("transactionReference") String transactionReference,
            HttpServletResponse response) {
        return RedirectUtil.redirect(response, service.settlement(ownerIdentifier, transactionReference));
    }

    @GetMapping("transactions/{transactionReference}")
    public ResponseEntity<TransactionDTO> get(
            @Valid @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\-]{1,34}[a-zA-Z0-9]$") @PathVariable("ownerIdentifier") String ownerIdentifier,
            @Valid @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\-]{1,34}[a-zA-Z0-9]$") @PathVariable("transactionReference") String transactionReference) {
        return ResponseEntity.ok(service.map(service.get(ownerIdentifier, transactionReference)));
    }


    @DeleteMapping("transactions/{transactionReference}")
    public ResponseEntity<TransactionDTO> cancel(
            @Valid @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\-]{1,34}[a-zA-Z0-9]$") @PathVariable("ownerIdentifier") String ownerIdentifier,
            @Valid @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9\\-]{1,34}[a-zA-Z0-9]$") @PathVariable("transactionReference") String transactionReference) {
        return ResponseEntity.ok(service.map(service.cancel(ownerIdentifier, transactionReference)));
    }
}
