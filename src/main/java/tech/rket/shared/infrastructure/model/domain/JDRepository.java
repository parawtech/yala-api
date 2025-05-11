package tech.rket.shared.infrastructure.model.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface JDRepository<T extends JDEntity> extends JpaRepository<T, BigInteger> {

}
