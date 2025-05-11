package tech.rket.payment.service.ipg;

import tech.rket.payment.domain.shared.IPG;
import tech.rket.payment.infrastructure.error.IpgServiceDoesNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpgFactory {
    static final Map<IPG, IpgService> IPG_SERVICE_MAP = new ConcurrentHashMap<>();

    public IpgService find(@Valid @NotNull IPG ipg) {
        if (ipg == null) {
            throw new IpgServiceDoesNotFoundException();
        }
        IpgService ipgService = IPG_SERVICE_MAP.get(ipg);
        if (ipgService == null) {
            throw new IpgServiceDoesNotFoundException();
        } else {
            return ipgService;
        }
    }
}
