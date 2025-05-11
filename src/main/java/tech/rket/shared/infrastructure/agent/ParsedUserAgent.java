package tech.rket.shared.infrastructure.agent;


public record ParsedUserAgent(Device device,
                              Agent agent,
                              OperatingSystem os) {
}


