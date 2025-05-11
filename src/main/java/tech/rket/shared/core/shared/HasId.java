package tech.rket.shared.core.shared;

public interface HasId<ID> {
    ID getId();

    interface Record<ID> extends HasId<ID> {
        ID id();

        default ID getId() {
            return id();
        }
    }
}
