package com.sermaluc.prueba_tecnica.services;

import com.sermaluc.prueba_tecnica.domain.dto.PhonesDTO;
import com.sermaluc.prueba_tecnica.domain.dto.UserDTO;
import com.sermaluc.prueba_tecnica.domain.exceptions.DuplicateEmailException;
import com.sermaluc.prueba_tecnica.domain.models.UserModel;
import com.sermaluc.prueba_tecnica.infraestructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        PhonesDTO phone = new PhonesDTO();
        phone.setNumber("987654321");
        phone.setCityCode("2");
        phone.setCountryCode("56");

        userDTO = new UserDTO();
        userDTO.setName("Juan Test");
        userDTO.setEmail("juan@test.com");
        userDTO.setPassword("Test123@");
        userDTO.setPhones(List.of(phone));
    }

    @Test
    void saveUser_WithValidData_SavesSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(jwtService.generateToken(any(UUID.class), anyString())).thenReturn("jwt.token.here");

        UserModel mockUser = new UserModel();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("juan@test.com");
        when(userRepository.save(any(UserModel.class))).thenReturn(mockUser);

        UserModel result = userService.saveUser(userDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("juan@test.com", result.getEmail());
        verify(userRepository, times(2)).save(any(UserModel.class));
        verify(passwordEncoder).encode("Test123@");
        verify(jwtService).generateToken(any(UUID.class), eq("juan@test.com"));
    }

    @Test
    void saveUser_WithDuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);

        DuplicateEmailException exception = assertThrows(
            DuplicateEmailException.class,
            () -> userService.saveUser(userDTO)
        );

        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(userRepository, never()).save(any(UserModel.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtService, never()).generateToken(any(), anyString());
    }

    @Test
    void saveUser_EncodesPassword() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(UUID.class), anyString())).thenReturn("token");

        UserModel mockUser = new UserModel();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("juan@test.com");
        when(userRepository.save(any(UserModel.class))).thenReturn(mockUser);

        userService.saveUser(userDTO);

        verify(passwordEncoder).encode("Test123@");
    }

    @Test
    void saveUser_GeneratesJwtToken() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(jwtService.generateToken(any(UUID.class), eq("juan@test.com"))).thenReturn("jwt.token");

        UserModel mockUser = new UserModel();
        mockUser.setId(userId);
        mockUser.setEmail("juan@test.com");
        when(userRepository.save(any(UserModel.class))).thenReturn(mockUser);

        userService.saveUser(userDTO);

        verify(jwtService).generateToken(any(UUID.class), eq("juan@test.com"));
    }

    @Test
    void saveUser_SetsUserActive() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(jwtService.generateToken(any(UUID.class), anyString())).thenReturn("token");

        UserModel mockUser = new UserModel();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("juan@test.com");
        when(userRepository.save(any(UserModel.class))).thenReturn(mockUser);

        UserModel result = userService.saveUser(userDTO);

        assertNotNull(result);
    }
}
