package pl.inz.costshare.server.mapper;

import org.springframework.stereotype.Component;
import pl.inz.costshare.server.dto.GroupDto;
import pl.inz.costshare.server.entity.GroupEntity;

@Component
public class GroupMapper {
    public GroupDto mapGroupEntitytoGroupDto(GroupEntity groupEntity, GroupDto groupDto) {
        groupDto.setId(groupEntity.getId());
        groupDto.setGroupName(groupEntity.getGroupName());
        return groupDto;
    }

    public GroupEntity mapGroupDtoToGroupEntity(GroupDto groupDto, GroupEntity groupEntity) {
        groupEntity.setId(groupDto.getId());
        groupEntity.setGroupName(groupDto.getGroupName());
        return groupEntity;
    }
}
