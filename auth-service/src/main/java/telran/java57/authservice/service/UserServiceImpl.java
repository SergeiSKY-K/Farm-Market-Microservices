package telran.java57.authservice.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import telran.java57.authservice.dao.UserRepository;
import telran.java57.authservice.dto.*;
import telran.java57.authservice.dto.exceptions.UserExistsException;
import telran.java57.authservice.dto.exceptions.UserNotFoundException;
import telran.java57.authservice.model.Role;
import telran.java57.authservice.model.UserAccount;

import java.util.List;



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserRegisterDto dto) {
        if (userRepository.existsById(dto.getLogin())) {
            throw new UserExistsException();
        }

        UserAccount userAccount = modelMapper.map(dto, UserAccount.class);
        userAccount.setPassword(passwordEncoder.encode(dto.getPassword()));
        userAccount.getRoles().add(Role.USER);

        userRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto getUser(String login) {
        UserAccount userAccount = userRepository.findById(login)
                .orElseThrow(() -> new UserNotFoundException(login));
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public void changePassword(String login, ChangePasswordDto dto) {

        UserAccount userAccount = userRepository.findById(login)
                .orElseThrow(() -> new UserNotFoundException(login));

        if (!passwordEncoder.matches(dto.getOldPassword(), userAccount.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password is too short");
        }

        userAccount.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(userAccount);
    }

    @Override
    public UserDto removeUser(String login) {
        UserAccount userAccount = userRepository.findById(login)
                .orElseThrow(() -> new UserNotFoundException(login));

        userRepository.delete(userAccount);

        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto updateUser(String login, UpdateUserDto dto) {
        UserAccount user = userRepository.findById(login)
                .orElseThrow(() -> new UserNotFoundException(login));

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
        UserAccount userAccount = userRepository.findById(login)
                .orElseThrow(() -> new UserNotFoundException(login));

        boolean changed;
        if (isAddRole) {
            changed = userAccount.addRole(role);
        } else {
            changed = userAccount.removeRole(role);
        }

        if (changed) {
            userRepository.save(userAccount);
        }

        return modelMapper.map(userAccount, RolesDto.class);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    @Override
    public List<UserDto> getAllSuppliers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Role.SUPPLIER))
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}
