package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor // Lombok
@Component
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private void loadSecurityData() {

        // beer auths
        Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());
        Authority updateBeer = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepository.save(Authority.builder().permission("beer.delete").build());

        // customer auths
        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        // brewery auths
        Authority createBrewery = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().permission("brewery.delete").build());

        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, updateBeer, readBeer, deleteBeer, createCustomer, readCustomer, updateCustomer, deleteCustomer,
                createBrewery, readBrewery, updateBrewery, deleteBrewery)));

//        adminRole.setAuthorities(Set.of(createBeer, updateBeer, readBeer, deleteBeer, createCustomer, readCustomer, updateCustomer, deleteCustomer,
//                createBrewery, readBrewery, updateBrewery, deleteBrewery));

        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery)));
//        customerRole.setAuthorities(Set.of(readBeer, readCustomer, readBrewery));

        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));
//        userRole.setAuthorities(Set.of(readBeer));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

//        Authority admin = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
//        Authority userRole = authorityRepository.save(Authority.builder().role("ROLE_USER").build());
//        Authority customer = authorityRepository.save(Authority.builder().role("ROLE_CUSTOMER").build());

        userRepository.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                //.authority(adminRole)
                .role(adminRole)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                //.authority(userRole)
                .role(userRole)
                .build());

        userRepository.save(User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                //.authority(customerRole)
                .role(customerRole)
                .build());

        log.debug("User loaded: " + userRepository.count());
    }

    @Transactional // without @Transactional, you get org.springframework.dao.InvalidDataAccessApiUsageException: detached entity passed to persist
    @Override
    public void run(String... args) throws Exception {
        if (authorityRepository.count() == 0) {
            loadSecurityData();
        }
    }


}
