package com.sermaluc.prueba_tecnica.services;

import com.sermaluc.prueba_tecnica.domain.dto.PhonesDTO;
import com.sermaluc.prueba_tecnica.domain.dto.UserDTO;
import com.sermaluc.prueba_tecnica.domain.exceptions.DuplicateEmailException;
import com.sermaluc.prueba_tecnica.domain.models.PhonesModel;
import com.sermaluc.prueba_tecnica.domain.models.UserModel;
import com.sermaluc.prueba_tecnica.infraestructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private String encodePassword(String rawPassword) {
        try {
            return passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            logger.error("Error encoding password: {}", e.getMessage());
            throw new RuntimeException("Error encoding password");
        }
    }

    public UserModel saveUser(UserDTO userDTO) {
        try {
            logger.info("Saving user with email: {}", userDTO.getEmail());

            if (userRepository.existsByEmail(userDTO.getEmail())) {
                logger.error("Email {} already exists", userDTO.getEmail());
                throw new DuplicateEmailException("El correo ya está registrado");
            }

            String encodedPassword = encodePassword(userDTO.getPassword());

            UserModel userModel = new UserModel();
            userModel.setName(userDTO.getName());
            userModel.setEmail(userDTO.getEmail());
            userModel.setPassword(encodedPassword);
            userModel.setLastLogin(LocalDateTime.now());

            List<PhonesModel> phonesList = userDTO.getPhones().stream()
                    .map(this::convertPhoneDtoToModel)
                    .collect(Collectors.toList());
            userModel.setPhones(phonesList);

            UserModel savedUser = userRepository.save(userModel);

            String jwtToken = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());
            savedUser.setToken(jwtToken);

            savedUser = userRepository.save(savedUser);
            logger.info("User saved successfully with id: {}", savedUser.getId());

            return savedUser;
        } catch (DuplicateEmailException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            throw new RuntimeException("Error al guardar el usuario");
        }
    }

    private PhonesModel convertPhoneDtoToModel(PhonesDTO dto) {
        PhonesModel model = new PhonesModel();
        model.setNumber(dto.getNumber());
        model.setCityCode(dto.getCityCode());
        model.setCountryCode(dto.getCountryCode());
        return model;
    }
}
