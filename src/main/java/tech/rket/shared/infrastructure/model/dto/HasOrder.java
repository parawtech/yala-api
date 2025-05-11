package tech.rket.shared.infrastructure.model.dto;

import java.io.Serializable;

public interface HasOrder extends Serializable {
    Integer getOrder();

    void setOrder(Integer id);
}