package app.demo.neurade.security;

import app.demo.neurade.domain.models.Role;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.repositories.RoleRepository;
import app.demo.neurade.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new UsernameNotFoundException("Role not found for user: " + username));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(role.getName())
        );

        return CustomUserDetails
                .builder()
                .user(user)
                .authorities(authorities)
                .build();
    }
}
