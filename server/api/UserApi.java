package pl.inz.costshare.server.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.inz.costshare.server.dto.*;
import pl.inz.costshare.server.security.SecurityUtils;
import pl.inz.costshare.server.service.GroupService;
import pl.inz.costshare.server.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserApi {

    private UserService userService;
    private GroupService groupService;

    public UserApi(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity getUser(@PathVariable("id") Long id) {
        UserDto userDto = userService.findUserById(id);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id: [" + id + "] does not exist");
        }
        return ResponseEntity.ok().body(userDto);
    }

    @GetMapping("/search")
    public ResponseEntity findUserByName(@RequestParam("userName") String userName) {

        UserDto userDto = userService.findUserByUserName(userName);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User [" + userName + "] does not exist");
        }
        return ResponseEntity.ok().body(userDto);

    }

    @GetMapping("/me")
    public ResponseEntity getMeUser() {
        Long myId = SecurityUtils.getCurrentUserId();
        return getUser(myId);
    }

    @GetMapping("/me/groups")
    public ResponseEntity getMyGroups() {
        Long myId = SecurityUtils.getCurrentUserId();
        List<GroupDto> groups = groupService.getGroupsForUser(myId);
        return ResponseEntity.ok().body(groups);
    }

    @GetMapping("/{id}/groups")
    public ResponseEntity getGroupsForUser(@PathVariable("id") Long id) {
        List<GroupDto> groups = groupService.getGroupsForUser(id);
        return ResponseEntity.ok().body(groups);
    }

    @PostMapping("/{userId}/groups")
    public ResponseEntity addUserToGroup(@PathVariable("userId") Long userId, @RequestBody AddUserToGroupDto addUserToGroupDto) {
        groupService.AddUserToGroup(userId, addUserToGroupDto.getId(), addUserToGroupDto.isAdmin());
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.ok().body(userService.createUser(createUserDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return ResponseEntity.ok().body(userService.updateUser(userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password-start")
    public ResponseEntity resetPasswordStart(@RequestBody ResetPasswordStartDto resetPasswordStartDto) {
        boolean ok = userService.resetPasswordStart(resetPasswordStartDto);
        if (ok) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(404).body("Niepoprawna nazwa użytkownika");
        }
    }

    @PostMapping("/reset-password-finish")
    public ResponseEntity resetPasswordFinish(@RequestBody ResetPasswordFinishDto resetPasswordFinishDto) {
        boolean ok = userService.resetPasswordFinish(resetPasswordFinishDto);
        if (ok) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(400).body("Niepoprawne dane");
        }
    }

}
