package telran.java57.authservice.service;

import telran.java57.authservice.dto.*;

import java.util.List;

public interface UserService {
    UserDto register(UserRegisterDto userRegisterDto);
    UserDto getUser(String login);
    void changePassword(String login, ChangePasswordDto dto);
    UserDto removeUser(String login);
    UserDto updateUser(String login, UpdateUserDto updateUserDto);
    RolesDto changeRolesList(String login, String role, boolean isAddRole);
    List<UserDto> getAllUsers();
    List<UserDto> getAllSuppliers();
}