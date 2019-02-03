package pl.inz.costshare.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.inz.costshare.server.entity.UserGroupEntity;

@Repository
public interface UserGroupRepository extends CrudRepository<UserGroupEntity, Long> {

}
