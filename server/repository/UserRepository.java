package pl.inz.costshare.server.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.inz.costshare.server.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByUserName(String userName);

    @Query("SELECT users FROM UserEntity users, UserGroupEntity usergroup" +
            " WHERE " +
            " usergroup.group.id = :groupId " +
            " AND " +
            " usergroup.user.id = users.id ")
    List<UserEntity> getUsersInGroup(@Param("groupId") Long groupId);
}


