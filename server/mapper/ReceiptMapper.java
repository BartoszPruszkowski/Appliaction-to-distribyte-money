package pl.inz.costshare.server.mapper;

import org.springframework.stereotype.Component;
import pl.inz.costshare.server.dto.ReceiptDto;
import pl.inz.costshare.server.entity.EventEntity;
import pl.inz.costshare.server.entity.ReceiptEntity;
import pl.inz.costshare.server.entity.UserEntity;
import pl.inz.costshare.server.repository.EventRepository;
import pl.inz.costshare.server.repository.UserRepository;

import javax.validation.ConstraintViolationException;

@Component
public class ReceiptMapper {

    private UserRepository userRepository;
    private EventRepository eventRepository;

    public ReceiptMapper(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public ReceiptDto mapReceiptEntityToReceiptDto(ReceiptEntity receiptEntity, ReceiptDto receiptDto) {
        receiptDto.setId(receiptEntity.getId());
        receiptDto.setFileName(receiptEntity.getFileName());
        receiptDto.setUploadUserId(receiptEntity.getUploadUser().getId());
        receiptDto.setEventId(receiptEntity.getEvent().getId());
        return receiptDto;
    }

    public ReceiptDto mapFullReceiptEntityToReceiptDto(ReceiptEntity receiptEntity, ReceiptDto receiptDto) {
        receiptDto = this.mapReceiptEntityToReceiptDto(receiptEntity, receiptDto);
        receiptDto.setRawData(receiptEntity.getRawData());
        return receiptDto;
    }

    public ReceiptEntity mapReceiptDtoToReceiptEntity(ReceiptDto receiptDto, ReceiptEntity receiptEntity) {
        UserEntity userEntity = userRepository.findById(receiptDto.getUploadUserId()).orElse(null);
        if (userEntity == null) {
            throw new ConstraintViolationException("User with id:" + receiptDto.getId() + " does not exist", null);
        }
        EventEntity eventEntity = eventRepository.findById(receiptDto.getEventId()).orElseGet(null);
        if (userEntity == null) {
            throw new ConstraintViolationException("Event with id:" + receiptDto.getEventId() + " does not exist", null);
        }

        receiptEntity.setFileName(receiptDto.getFileName());
        receiptEntity.setRawData(receiptDto.getRawData());
        receiptEntity.setUploadUser(userEntity);
        receiptEntity.setEvent(eventEntity);
        return receiptEntity;
    }

}
