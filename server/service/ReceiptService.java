package pl.inz.costshare.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.inz.costshare.server.dto.ReceiptDto;
import pl.inz.costshare.server.entity.ReceiptEntity;
import pl.inz.costshare.server.mapper.ReceiptMapper;
import pl.inz.costshare.server.repository.ReceiptRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReceiptService {

    private ReceiptRepository receiptRepository;
    private ReceiptMapper receiptMapper;

    public ReceiptService(ReceiptRepository receiptRepository, ReceiptMapper receiptMapper) {
        this.receiptRepository = receiptRepository;
        this.receiptMapper = receiptMapper;
    }

    public List<ReceiptDto> getReceiptsByUploadUser(Long userId) {
        List<ReceiptEntity> result = receiptRepository.findByUploadUserId(userId);
        List<ReceiptDto> receiptDtos = new ArrayList<>();
        result.forEach(receiptEntity -> {
            receiptDtos.add(receiptMapper.mapReceiptEntityToReceiptDto(receiptEntity, new ReceiptDto()));
        });
        return receiptDtos;
    }

    public ReceiptDto findReceiptById(Long id) {
        ReceiptEntity receiptEntity = receiptRepository.findById(id).orElse(null);
        if (receiptEntity == null) {
            return null;
        }
        ReceiptDto receiptDto = receiptMapper.mapFullReceiptEntityToReceiptDto(receiptEntity, new ReceiptDto());
        return receiptDto;
    }

    @Transactional
    public ReceiptDto createReceipt(ReceiptDto receiptDto) {
        ReceiptEntity receiptEntity = receiptMapper.mapReceiptDtoToReceiptEntity(receiptDto, new ReceiptEntity());
        receiptEntity = receiptRepository.save(receiptEntity);
        ReceiptDto resultReceiptDto = receiptMapper.mapReceiptEntityToReceiptDto(receiptEntity, new ReceiptDto());
        return resultReceiptDto;
    }

    public List<ReceiptDto> getReceiptsForEvent(Long eventId) {
        List<ReceiptEntity> result = receiptRepository.findByEventId(eventId);
        List<ReceiptDto> receiptDtos = new ArrayList<>();
        result.forEach(receiptEntity -> {
            receiptDtos.add(receiptMapper.mapReceiptEntityToReceiptDto(receiptEntity, new ReceiptDto()));
        });
        return receiptDtos;
    }

}
