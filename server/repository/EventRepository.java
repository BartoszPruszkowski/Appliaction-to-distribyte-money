package pl.inz.costshare.server.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.inz.costshare.server.entity.EventEntity;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<EventEntity, Long> {
    @Query("SELECT event FROM EventEntity event WHERE  event.group.id = :groupId ")
    List<EventEntity> getEventsForGroup(@Param("groupId") Long groupId);
}
