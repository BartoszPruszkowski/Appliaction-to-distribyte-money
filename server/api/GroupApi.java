package pl.inz.costshare.server.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.inz.costshare.server.dto.EventDto;
import pl.inz.costshare.server.dto.GroupDto;
import pl.inz.costshare.server.dto.UserDto;
import pl.inz.costshare.server.service.EventService;
import pl.inz.costshare.server.service.GroupService;
import pl.inz.costshare.server.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupApi {

    private UserService userService;
    private GroupService groupService;
    private EventService eventService;

    public GroupApi(GroupService groupService, UserService userService, EventService eventService) {
        this.groupService = groupService;
        this.eventService = eventService;
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity getAllGroups() {
        return ResponseEntity.ok().body(groupService.getAllGroups());
    }


    @GetMapping("/{id}")
    public ResponseEntity getGroup(@PathVariable("id") Long id) {
        GroupDto groupDto = groupService.findGroupById(id);
        if (groupDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group with name: [" + id + "] does not exist");
        }
        return ResponseEntity.ok().body(groupDto);
    }

    @PostMapping
    public ResponseEntity createGroup(@RequestBody GroupDto groupDto) {
        return ResponseEntity.ok().body(groupService.createGroup(groupDto));
    }

    @GetMapping("/{id}/users")
    public ResponseEntity getUserInGroup(@PathVariable("id") Long id) {
        List<UserDto> users = userService.getUsersInGroup(id);
        return ResponseEntity.ok().body(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateGroup(@PathVariable("id") Long id, @RequestBody GroupDto groupDto) {
        groupDto.setId(id);
        return ResponseEntity.ok().body(groupService.updateGroup(groupDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGroup(@PathVariable("id") Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/events")
    public ResponseEntity getEventsForGroup(@PathVariable("id") Long id) {
        List<EventDto> events = eventService.getEventsForGroups(id);
        return ResponseEntity.ok().body(events);
    }
}