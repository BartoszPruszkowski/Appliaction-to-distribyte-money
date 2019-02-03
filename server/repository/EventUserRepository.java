package pl.inz.costshare.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.inz.costshare.server.entity.EventUserEntity;

@Repository
public interface EventUserRepository extends CrudRepository<EventUserEntity, Long> {

}

