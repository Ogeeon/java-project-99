package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserPatchDTO;
import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND = "User with id %d not found";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream().map(userMapper::map).toList();
    }

    @Override
    public UserResponseDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
        return userMapper.map(user);
    }

    @Override
    public UserResponseDTO create(UserCreateDTO dto) {
        var model = userMapper.map(dto);
        return userMapper.map(userRepository.save(model));
    }

    @Override
    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        var user = getUserOrThrow(id);
        userMapper.update(dto, user);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public UserResponseDTO partialUpdate(Long id, UserPatchDTO dto) {
        var user = getUserOrThrow(id);
        userMapper.patch(dto, user);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        getUserOrThrow(id);
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND.formatted(id)));
    }
}
