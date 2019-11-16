package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoPassword;
import com.nbu.annotationplus.dto.DtoUser;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.persistence.entity.Role;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.RoleRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import com.nbu.annotationplus.utils.AuthUtils;
import com.nbu.annotationplus.utils.UserUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<DtoUser> createUser(DtoUser dtoUser) {
        UserUtils.validateFirstAndLastUserName(dtoUser.getName(),dtoUser.getLastName());
        UserUtils.validateEmail((dtoUser.getEmail()));
        UserUtils.validatePassword(dtoUser.getPassword());
        User existingUser = userRepository.findByEmail(dtoUser.getEmail());
        if (existingUser != null){
            throw new InvalidInputParamsException("User with that email already exists");
        }
        User user = new User();
        user.setEmail(dtoUser.getEmail().trim());
        user.setName(dtoUser.getName().trim());
        user.setLastName(dtoUser.getLastName().trim());
        user.setPassword(bCryptPasswordEncoder.encode(dtoUser.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
        return new ResponseEntity<DtoUser>(toDtoUser(user),HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<DtoUser> updateCurrentUser(DtoUser dtoUser){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        UserUtils.validateFirstAndLastUserName(dtoUser.getName(),dtoUser.getLastName());
        currentUser.setName(dtoUser.getName().trim());
        currentUser.setLastName(dtoUser.getLastName().trim());
        currentUser.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
        userRepository.save(currentUser);
        return new ResponseEntity<DtoUser>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> updatePassword (DtoPassword dtoPassword){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        validateUpdatePassword(dtoPassword);
        currentUser.setPassword(bCryptPasswordEncoder.encode(dtoPassword.getConfirmNewPassword()));
        currentUser.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
        userRepository.save(currentUser);
        return ResponseEntity.ok().build();
    }

    public DtoUser getCurrentUser(){
        Authentication authentication = validateUser();
        return toDtoUser(findUserByEmail(authentication.getName()));
    }

    public Long getUserId(){
       DtoUser dtoUser =  getCurrentUser();
       return dtoUser.getId();
    }

    private String getCurrentUserPassword(){
        Authentication authentication = validateUser();
        User user = userRepository.findByEmail(authentication.getName());
        return user.getPassword();
    }

    private Authentication validateUser() {
        Authentication authentication = AuthUtils.getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        else {
            return authentication;
        }
    }

    private void validateUpdatePassword(DtoPassword dtoPassword) {
        if (!bCryptPasswordEncoder.matches(dtoPassword.getPassword(),getCurrentUserPassword())) {
            throw new InvalidInputParamsException("Invalid Current Password");
        }
        UserUtils.validatePassword(dtoPassword.getNewPassword());
        if (!dtoPassword.getConfirmNewPassword().equals(dtoPassword.getNewPassword())) {
            throw new InvalidInputParamsException(UserUtils.PASSWORDS_NOT_THE_SAME_ERROR);
        }
        if (bCryptPasswordEncoder.matches(dtoPassword.getConfirmNewPassword(),getCurrentUserPassword())) {
            throw new InvalidInputParamsException("New Password cannot be the same as your Current Password");
        }
    }

    private DtoUser toDtoUser(User user) {
        ModelMapper modelMapper = new ModelMapper();
        DtoUser dtoUser = modelMapper.map(user, DtoUser.class);
        dtoUser.setPassword(null);
        dtoUser.setRoles(null);
        //dtoUser.setId(null);
        return dtoUser;
    }
}