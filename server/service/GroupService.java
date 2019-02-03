package pl.inz.costshare.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.inz.costshare.server.dto.GroupDto;
import pl.inz.costshare.server.entity.GroupEntity;
import pl.inz.costshare.server.entity.UserEntity;
import pl.inz.costshare.server.entity.UserGroupEntity;
import pl.inz.costshare.server.exception.ResourceNotFoundException;
import pl.inz.costshare.server.mapper.GroupMapper;
import pl.inz.costshare.server.repository.GroupRepository;
import pl.inz.costshare.server.repository.UserGroupRepository;
import pl.inz.costshare.server.repository.UserRepository;
import pl.inz.costshare.server.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GroupService {

    private GroupRepository groupRepository;
    private UserGroupRepository userGroupRepository;
    private UserRepository userRepository;
    private GroupMapper groupMapper;

    public GroupService(
        GroupRepository groupRepository,
        UserGroupRepository userGroupRepository,
        UserRepository userRepository,
        GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.groupMapper = groupMapper;
    }

    public List<GroupDto> getAllGroups() {
        Iterable<GroupEntity> result = groupRepository.findAll();
        List<GroupDto> allGroups = new ArrayList<>();
        result.forEach(groupEntity ->
            allGroups.add(groupMapper.mapGroupEntitytoGroupDto(groupEntity, new GroupDto())));
        return allGroups;
    }


    public GroupDto findGroupById(Long id) {
        GroupEntity groupEntity = groupRepository.findById(id).orElse(null);
        GroupDto groupDto = groupMapper.mapGroupEntitytoGroupDto(groupEntity, new GroupDto());
        return groupDto;
    }




    @Transactional
    public void AddUserToGroup(Long userId, Long groupId, boolean admin) {
        UserGroupEntity userGroupEntity = new UserGroupEntity();
        GroupEntity groupEntity = groupRepository.findById(groupId).get();
        UserEntity userEntity = userRepository.findById(userId).get();
        userGroupEntity.setAdmin(admin);
        userGroupEntity.setUser(userEntity);
        userGroupEntity.setGroup(groupEntity);
        userGroupEntity = userGroupRepository.save(userGroupEntity);
    }

    @Transactional
    public GroupDto createGroup(GroupDto groupDto) {
        GroupEntity groupEntity = groupMapper.mapGroupDtoToGroupEntity(groupDto, new GroupEntity());
        groupEntity = groupRepository.save(groupEntity);
        UserGroupEntity userGroupEntity = new UserGroupEntity();
        userGroupEntity.setAdmin(true);
        userGroupEntity.setGroup(groupEntity);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity currentUserEntity = userRepository.findById(currentUserId).get();
        userGroupEntity.setUser(currentUserEntity);
        userGroupEntity = userGroupRepository.save(userGroupEntity);
        GroupDto dto = groupMapper.mapGroupEntitytoGroupDto(groupEntity, new GroupDto());
        return dto;
    }

    public List<GroupDto> getGroupsForUser(Long userId) {
        List<GroupEntity> result = groupRepository.getGroupsForUser(userId);
        List<GroupDto> groupDtos = new ArrayList<>();
        result.forEach(groupEntity -> {
            groupDtos.add(groupMapper.mapGroupEntitytoGroupDto(groupEntity, new GroupDto()));
        });
        return groupDtos;
    }


    @Transactional    public GroupDto updateGroup(GroupDto groupDto) {
        GroupEntity groupEntity = groupRepository.findById(groupDto.getId()).orElse(null);
        if (groupEntity == null) {
            throw new ResourceNotFoundException("Group with id [" + groupDto.getId() + "] does not exist");
        }
        groupMapper.mapGroupDtoToGroupEntity(groupDto, groupEntity);
        groupEntity = groupRepository.save(groupEntity);
        GroupDto dto = groupMapper.mapGroupEntitytoGroupDto(groupEntity, new GroupDto());
        return dto;
    }

    @Transactional
    public void deleteGroup(Long id) {
        GroupEntity groupEntity = groupRepository.findById(id).orElse(null);
        if (groupEntity == null) {
            throw new ResourceNotFoundException("Group with id [" + id + "] does not exist");
        }
        groupRepository.delete(groupEntity);
    }

}


