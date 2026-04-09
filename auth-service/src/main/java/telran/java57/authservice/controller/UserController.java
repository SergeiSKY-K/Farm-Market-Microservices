package telran.java57.authservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telran.java57.authservice.dto.*;
import telran.java57.authservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserDto register(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto);
    }

    @PreAuthorize("#login == authentication.name or hasRole('ADMINISTRATOR')")
    @DeleteMapping("/user/{login}")
    public UserDto removeUser(@PathVariable String login) {
        return userService.removeUser(login);
    }

    @PreAuthorize("#login == authentication.name or hasRole('ADMINISTRATOR')")
    @GetMapping("/user/{login}")
    public UserDto getUserByLogin(@PathVariable String login) {
        return userService.getUser(login);
    }

    @PreAuthorize("#login == authentication.name or hasRole('ADMINISTRATOR')")
    @PutMapping("/user/{login}")
    public UserDto updateUser(@PathVariable String login, @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(login, updateUserDto);
    }

    @PutMapping("/user/{login}/role/{role}")
    public RolesDto addRole(@PathVariable String login, @PathVariable String role) {
        return userService.changeRolesList(login, role, true);
    }

    @DeleteMapping("/user/{login}/role/{role}")
    public RolesDto removeRole(@PathVariable String login, @PathVariable String role) {
        return userService.changeRolesList(login, role, false);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestHeader("X-User-Login") String login,
            @RequestBody ChangePasswordDto body
    ) {
        userService.changePassword(login, body);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/suppliers")
    public List<UserDto> getAllSuppliers() {
        return userService.getAllSuppliers();
    }
}
