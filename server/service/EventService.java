package pl.inz.costshare.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.inz.costshare.server.dto.EventDto;
import pl.inz.costshare.server.dto.EventUserDto;
import pl.inz.costshare.server.entity.EventEntity;
import pl.inz.costshare.server.entity.EventUserEntity;
import pl.inz.costshare.server.entity.UserEntity;
import pl.inz.costshare.server.exception.ResourceNotFoundException;
import pl.inz.costshare.server.mapper.EventMapper;
import pl.inz.costshare.server.repository.EventRepository;
import pl.inz.costshare.server.repository.EventUserRepository;
import pl.inz.costshare.server.repository.UserRepository;
import pl.inz.costshare.server.security.SecurityUtils;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventService {

    private EventRepository eventRepository;
    private EventMapper eventMapper;
    private UserRepository userRepository;
    private EventUserRepository eventUserRepository;

    public EventService(EventRepository eventRepository, EventMapper eventMapper, UserRepository userRepository, EventUserRepository eventUserRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userRepository = userRepository;
        this.eventUserRepository = eventUserRepository;
    }

    public EventDto findEventById(Long id) {
        EventEntity eventEntity = eventRepository.findById(id).orElse(null);
        if (eventEntity == null) {
            return null;
        }
        EventDto eventDto = new EventDto();
        eventMapper.mapEventEntityToEventDto(eventEntity, eventDto); //zmapuj wartość encji na dto
        return eventDto;
    }

    public List<EventDto> getAllEvents() {
        Iterable<EventEntity> result = eventRepository.findAll();
        List<EventDto> allEvents = new ArrayList<>();
        result.forEach(eventEntity ->
            allEvents.add(eventMapper.mapEventEntityToEventDto(eventEntity, new EventDto())));
        return allEvents;
    }

    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        EventEntity eventEntity = eventMapper.mapEventDtoToEventEntity(eventDto, new EventEntity());
        eventEntity.setSettled(false);
        eventEntity = eventRepository.save(eventEntity); //to co zwraca save moge używać

        for (EventUserDto eventUserDto : eventDto.getEventUsers()) {
            UserEntity userEntity = userRepository.findById(eventUserDto.getUserId()).get();
            EventUserEntity eventUserEntity = new EventUserEntity();
            eventUserEntity.setUser(userEntity);
            eventUserEntity.setEvent(eventEntity);
            eventUserEntity.setUserEventType("deptor");
            eventUserEntity.setCost(eventUserDto.getCost());
            eventUserEntity.setSettled(false);
            eventUserRepository.save(eventUserEntity);

        }

        EventUserEntity myEventUserEntity = new EventUserEntity();
        Long currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity currentUser = userRepository.findById(currentUserId).get();
        myEventUserEntity.setUser(currentUser);
        myEventUserEntity.setUserEventType("creditor");
        myEventUserEntity.setEvent(eventEntity);
        myEventUserEntity.setCost(0.0);
        myEventUserEntity.setSettled(false);
        eventUserRepository.save(myEventUserEntity);

        EventDto result = eventMapper.mapEventEntityToEventDto(eventEntity, new EventDto());
        return result;
    }

    @Transactional
    public void deleteEvent(Long id) {
        EventEntity eventEntity = eventRepository.findById(id).orElse(null);
        if (eventEntity == null) {
            throw new ResourceNotFoundException("Event with id [" + id + "] does not exist");
        }
        eventRepository.delete(eventEntity);
    }

    public List<EventDto> getEventsForGroups(Long groupId) {
        List<EventEntity> result = eventRepository.getEventsForGroup(groupId);
        List<EventDto> eventDtos = new ArrayList<>();
        result.forEach(eventEntity -> {
            eventDtos.add(eventMapper.mapEventEntityToEventDto(eventEntity, new EventDto()));
        });
        return eventDtos;
    }

    @Transactional
    public void settleUpEventUser(Long eventId, Long userId) {
        EventEntity event = eventRepository.findById(eventId).orElseGet(null);
        if (event == null) {
            throw new ResourceNotFoundException("Event with id [" + eventId + "] does not exist");
        }

        EventUserEntity eventUser = null;
        for (EventUserEntity eUser : event.getEventUserEntities()) {
            if (eUser.getUser().getId().equals(userId)) {
                eventUser = eUser;
                break;
            }
        }

        if (eventUser == null) {
            throw new ConstraintViolationException("User with id:" + userId + " does not exist in event", null);
        }

        if (Boolean.TRUE.equals(eventUser.getSettled())) {
            throw new ConstraintViolationException("User with id:" + userId + " is already settled up", null);
        }

        eventUser.setSettled(true);

        boolean allSettled = true;
        for (EventUserEntity eUser : event.getEventUserEntities()) {
            if (eUser.getId().equals(eventUser.getId())) {
                continue;
            }
            if ("deptor".equals(eUser.getUserEventType()) && !Boolean.TRUE.equals(eUser.getSettled())) {
                allSettled = false;
                break;
            }
        }

        eventUserRepository.save(eventUser);
        if (allSettled) {
            event.setSettled(true);
            eventRepository.save(event);
        }
    }
}
