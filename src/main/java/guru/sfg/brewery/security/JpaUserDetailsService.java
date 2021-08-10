package guru.sfg.brewery.security;

import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // Lombok @RequiredArgsConstructor : will get constructor for this and spring framework will do the dependency injection

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Getting User info via JPA");

        return userRepository.findByUsername(username).orElseThrow(() -> {
            return new UsernameNotFoundException("Username: " + username + " not found");
        });
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
//                user.getEnabled(), user.getAccountNonExpired(), user.getCredentialsNonExpired(),
//                user.getAccountNonLocked(), convertToSpringAuthorities(user.getAuthorities()));
    }

//    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
//        if (authorities != null && authorities.size() > 0) {
//            return authorities.stream()
//                    .map(Authority::getPermission)
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toSet());
//        } else {
//            return new HashSet<>();
//        }
//    }
}
