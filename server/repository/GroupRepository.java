package pl.inz.costshare.server.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.inz.costshare.server.entity.GroupEntity;

import java.util.List;


@Repository
public interface GroupRepository extends CrudRepository<GroupEntity, Long> {

    @Query("SELECT groups FROM GroupEntity groups, UserGroupEntity usergroup" +
            " WHERE " +
            " usergroup.user.id = :userId " +
            " AND " +
            " usergroup.group.id = groups.id ")
    List<GroupEntity> getGroupsForUser(@Param("userId") Long userId);
}


