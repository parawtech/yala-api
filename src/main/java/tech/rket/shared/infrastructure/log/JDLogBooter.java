package tech.rket.shared.infrastructure.log;

public interface JDLogBooter {
    void boot(JDLogger logger);

    void reboot(JDLogger logger);
}
