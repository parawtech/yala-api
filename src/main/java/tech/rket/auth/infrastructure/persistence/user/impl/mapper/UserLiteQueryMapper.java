package tech.rket.auth.infrastructure.persistence.user.impl.mapper;

import org.mapstruct.Mapper;
import tech.rket.auth.domain.query.user.UserLite;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;

@Mapper(config = MapstructConfig.class)
public interface UserLiteQueryMapper extends PersistenceMapper<UserEntity, UserLite> {
}
