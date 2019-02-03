package pl.inz.costshare.server.mapper;

import org.springframework.stereotype.Component;
import pl.inz.costshare.server.dto.UserDto;
import pl.inz.costshare.server.entity.UserEntity;

@Component
public class UserMapper {

    public UserDto mapUserEntityToUserDto(UserEntity userEntity, UserDto userDto) {
        userDto.setId(userEntity.getId());
        userDto.setUserName(userEntity.getUserName());
        userDto.setPhoneNo(userEntity.getPhoneNo());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        return userDto;
    }

    public UserEntity mapUserDtoToUserEntity(UserDto userDto, UserEntity userEntity) {
        userEntity.setUserName(userDto.getUserName());
        userEntity.setPhoneNo(userDto.getPhoneNo());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        return userEntity;
    }

}
