package pl.inz.costshare.server.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.inz.costshare.server.dto.EventDto;
import pl.inz.costshare.server.exception.ResourceNotFoundException;
import pl.inz.costshare.server.service.EventService;

@RestController
@RequestMapping("/events")
public class EventApi {
    private EventService eventService;

    public EventApi(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity getAllEvents() {
        return ResponseEntity.ok().body(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity getEventById(@PathVariable("id") Long id) {
        EventDto eventDto = eventService.findEventById(id);
        if (eventDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event with id: [" + id + "] does not exist");
        }
        return ResponseEntity.ok().body(eventDto);
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto) {
        return ResponseEntity.ok().body(eventService.createEvent(eventDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/event-users/{userId}/settled")
    public ResponseEntity settleEventUser(
        @PathVariable("eventId") Long eventId,
        @PathVariable("userId") Long userId,
        @RequestBody Boolean settled) {

        if (!Boolean.TRUE.equals(settled)) {
            throw new ResourceNotFoundException("Only true is accepted");
        }

        eventService.settleUpEventUser(eventId, userId);

        return ResponseEntity.ok().build();
    }

}