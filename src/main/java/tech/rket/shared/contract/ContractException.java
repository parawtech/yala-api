package tech.rket.shared.contract;

public class ContractException extends RuntimeException {
    private final Integer status;

    public ContractException(String code, Integer status, Exception e) {
        super(code, e);
        this.status = status;
    }

    public String getStatus() {
        return status.toString();
    }
}
