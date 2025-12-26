package telran.java57.authservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import telran.java57.authservice.dao.UserRepository;
import telran.java57.authservice.model.UserAccount;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));



        Collection<String> authorities = userAccount.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .toList();



        return new User(
                username,
                userAccount.getPassword(),
                AuthorityUtils.createAuthorityList(authorities)
        );
    }
}
