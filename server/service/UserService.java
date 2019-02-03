package pl.inz.costshare.server.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.inz.costshare.server.dto.CreateUserDto;
import pl.inz.costshare.server.dto.ResetPasswordFinishDto;
import pl.inz.costshare.server.dto.ResetPasswordStartDto;
import pl.inz.costshare.server.dto.UserDto;
import pl.inz.costshare.server.entity.UserEntity;
import pl.inz.costshare.server.exception.ResourceNotFoundException;
import pl.inz.costshare.server.mapper.UserMapper;
import pl.inz.costshare.server.repository.UserRepository;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private JavaMailSender mailSender;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public List<UserDto> getAllUsers() {
        Iterable<UserEntity> result = userRepository.findAll();
        List<UserDto> allUsers = new ArrayList<>();
        result.forEach(userEntity -> {
            allUsers.add(userMapper.mapUserEntityToUserDto(userEntity, new UserDto()));
        });
        return allUsers;
    }

    public UserDto findUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        return userEntity == null ? null : userMapper.mapUserEntityToUserDto(userEntity, new UserDto());
    }

    public UserDto findUserByUserName(String userName) {
        UserEntity userEntity = userRepository.findByUserName(userName);
        return userEntity == null ? null : userMapper.mapUserEntityToUserDto(userEntity, new UserDto());
    }

    @Transactional
    public UserDto createUser(CreateUserDto user) {
        if (!user.getPassword().equals(user.getPasswordRepeated())) {
            throw new ConstraintViolationException("Passwords do not match", null);
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        UserEntity entity = userMapper.mapUserDtoToUserEntity(user, new UserEntity());
        entity.setPassword(encodedPassword);
        entity = userRepository.save(entity);
        UserDto dto = userMapper.mapUserEntityToUserDto(entity, new UserDto());
        return dto;
    }

    public List<UserDto> getUsersInGroup(Long groupId) {
        List<UserEntity> result = userRepository.getUsersInGroup(groupId);
        List<UserDto> userDtos = new ArrayList<>();
        result.forEach(userEntity -> {
            userDtos.add(userMapper.mapUserEntityToUserDto(userEntity, new UserDto()));
        });
        return userDtos;
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = userRepository.findById(userDto.getId()).orElse(null);
        if (userEntity == null) {
            throw new ResourceNotFoundException("User with id [" + userDto.getId() + "] does not exist");
        }
        userMapper.mapUserDtoToUserEntity(userDto, userEntity);
        userEntity = userRepository.save(userEntity);
        UserDto dto = userMapper.mapUserEntityToUserDto(userEntity, new UserDto());
        return dto;
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity == null) {
            throw new ResourceNotFoundException("User with id [" + id + "] does not exist");
        }
        userRepository.delete(userEntity);
    }

    @Transactional
    public boolean resetPasswordFinish(ResetPasswordFinishDto resetPasswordFinishDto) {
        // pobierz usera o danej nazwie
        UserEntity userEntity = userRepository.findByUserName(resetPasswordFinishDto.getUserName());

        // sprawdz wszystko
        if (userEntity == null) {
            return false;
        }

        // sprawdz wszystko
        if (userEntity.getResetPasswordCode() == null) {
            return false;
        }

        if (!userEntity.getResetPasswordCode().equals(resetPasswordFinishDto.getCode())) {
            return false;
        }

        if (!resetPasswordFinishDto.getNewPassword().equals(resetPasswordFinishDto.getNewPasswordRepeat())) {
            return false;
        }

        // aktualizuj haslo i usun kod
        String encodedPassword = passwordEncoder.encode(resetPasswordFinishDto.getNewPassword());
        userEntity.setPassword(encodedPassword);
        userEntity.setResetPasswordCode(null);
        userRepository.save(userEntity);

        return true;
    }

    @Transactional
    public boolean resetPasswordStart(ResetPasswordStartDto resetPasswordStartDto) {
        // pobierz usera o danej nazwie
        UserEntity userEntity = userRepository.findByUserName(resetPasswordStartDto.getUserName());
        if (userEntity == null) {
            throw new ConstraintViolationException("User with name: [" + resetPasswordStartDto.getUserName() + "] does not exist", null);
        }
        // wygeneruj kod
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            code += random.nextInt(11); //losuje od 0 do 10, czyli wsrod liczb mniejszych od 11
        }

        // zapisz kod na encji
        userEntity.setResetPasswordCode(code);
        userRepository.save(userEntity);

        // wyslij maila z kodem
        String subject = "CostShare password reset";
        String emailText = createEmailText(code, userEntity);
        String emailAddress = userEntity.getUserName(); // user name to email
        sendEmail(emailAddress, subject, emailText);

        return true;
    }

    private String createEmailText(String code, UserEntity userEntity) {
        String text =
            "Hi " + userEntity.getFirstName() + ".\n" +
                "Your reset password code is: " + code + "\n\n";
        return text;
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
