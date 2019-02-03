package pl.inz.costshare.server.mapper;

import org.springframework.stereotype.Component;
import pl.inz.costshare.server.dto.EventDto;
import pl.inz.costshare.server.dto.EventUserDto;
import pl.inz.costshare.server.entity.EventEntity;
import pl.inz.costshare.server.entity.EventUserEntity;
import pl.inz.costshare.server.entity.GroupEntity;
import pl.inz.costshare.server.entity.UserEntity;
import pl.inz.costshare.server.repository.GroupRepository;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventMapper {
    private GroupRepository groupRepository;


    public EventMapper(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public EventDto mapEventEntityToEventDto(EventEntity eventEntity, EventDto eventDto) {
        eventDto.setId(eventEntity.getId());
        eventDto.setName(eventEntity.getName());
        eventDto.setGroupId(eventEntity.getGroup().getId());
        eventDto.setSettled(eventEntity.getSettled());

        List<EventUserDto> userDtoList = new ArrayList<>(); //tworzyy pusta liste  z EventUserDto
        //iterujemy po liscie eventuserentity
        for (EventUserEntity eventUserEntity : eventEntity.getEventUserEntities()) {
            //tworymy puste eventuserdto
            EventUserDto eventUserDto = new EventUserDto();
            //przepisujemy wartosci z userentity do dto
            eventUserDto.setId(eventUserEntity.getId());
            eventUserDto.setCost(eventUserEntity.getCost());
            eventUserDto.setSettled(eventUserEntity.getSettled());
            eventUserDto.setUserEventType(eventUserEntity.getUserEventType());
            eventUserDto.setUserId(eventUserEntity.getUser().getId());
            eventUserDto.setEventId(eventUserEntity.getEvent().getId());

            //TODO: zaimplementuj metode: public EventUserDto mapEventUserEntityToEventUserDto(Entity, Dto)
            //dodajemy obiekt dto do listy
            userDtoList.add(eventUserDto);
        }
        eventDto.setEventUsers(userDtoList);

        return eventDto;
    }

    public EventEntity mapEventDtoToEventEntity(EventDto eventDto, EventEntity eventEntity) {
        GroupEntity groupEntity = groupRepository.findById(eventDto.getGroupId()).orElse(null);

        if (groupEntity == null) {
            throw new ConstraintViolationException("Group with id:" + eventDto.getGroupId() + " does not exist", null);
        }
        eventEntity.setGroup(groupEntity);
        eventEntity.setName(eventDto.getName());
        eventEntity.setSettled(eventDto.getSettled());

        return eventEntity;
    }
}
