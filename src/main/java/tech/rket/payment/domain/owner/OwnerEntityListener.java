package tech.rket.payment.domain.owner;

import jakarta.persistence.PostLoad;

public class OwnerEntityListener {
    @PostLoad
    public void postLoad(Owner owner) {
        owner.getSelfConfig().validate();
    }
}
