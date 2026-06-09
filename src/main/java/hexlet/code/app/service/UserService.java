package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAll();

    UserResponseDTO findById(Long id);

    UserResponseDTO create(UserCreateDTO dto);

    UserResponseDTO update(Long id, UserUpdateDTO dto);

    UserResponseDTO partialUpdate(Long id, UserPatchDTO dto);

    void delete(Long id);
}
