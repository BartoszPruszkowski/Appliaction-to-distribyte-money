package pl.inz.costshare.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.inz.costshare.server.entity.ReceiptEntity;

import java.util.List;

@Repository
public interface ReceiptRepository extends CrudRepository<ReceiptEntity, Long> {

    List<ReceiptEntity> findByUploadUserId(Long id);

    List<ReceiptEntity> findByEventId(Long id);

}
