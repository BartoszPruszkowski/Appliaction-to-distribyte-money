package pl.inz.costshare.server.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.inz.costshare.server.entity.UserEntity;
import pl.inz.costshare.server.repository.UserRepository;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUserName(userName);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User " + userName + " was not found");
        }
        UserDetails userDetails = new CostShareUserDetails(
            userEntity.getUserName(),
            userEntity.getPassword(),
            Collections.emptyList(),
            userEntity.getId()
        );
        return userDetails;
    }
}
