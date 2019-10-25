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
import com.nbu.annotationplus.utils.ParseUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResponseEntity<DtoUser> saveUser(DtoUser dtoUser) {
        validateFirstAndLastName(dtoUser);
        validateEmail(dtoUser);
        validatePassword(dtoUser.getPassword());
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
        return new ResponseEntity<DtoUser>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<DtoUser> updateUser(DtoUser dtoUser){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        validateFirstAndLastName(dtoUser);
        currentUser.setName(dtoUser.getName().trim());
        currentUser.setLastName(dtoUser.getLastName().trim());
        userRepository.save(currentUser);
        return new ResponseEntity<DtoUser>(HttpStatus.OK);
    }

    //private StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();

    @Transactional
    public ResponseEntity<?> updatePassword (DtoPassword dtoPassword){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        validateUpdatePassword(dtoPassword);
        currentUser.setPassword(bCryptPasswordEncoder.encode(dtoPassword.getConfirmNewPassword()));
        userRepository.save(currentUser);
        return ResponseEntity.ok().build();
    }

    public DtoUser getCurrentUser(){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        return toDtoUser(userRepository.findByEmail(userEmail));
    }

    public Long getUserId(){
       DtoUser dtoUser =  getCurrentUser();
       return dtoUser.getId();
    }

    private String getCurrentUserPassword(){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        return user.getPassword();
    }

    /*public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }*/

   /* public void updatePassword(String password, Long userId){
        userRepository.updatePassword(password, userId);
    }*/

    private Authentication validateUser() {
        Authentication authentication = AuthUtils.getAuthenticatedUser();
        if (authentication == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        else {
            return authentication;
        }
    }

    private void validateFirstAndLastName(DtoUser user){
        if(ParseUtils.validateTitle(user.getName())){
            throw new InvalidInputParamsException("Invalid First Name");
        }
        if(ParseUtils.validateTitle(user.getLastName())){
            throw new InvalidInputParamsException("Invalid Last Name");
        }
    }

    private void validateEmail(DtoUser user){
        if(!ParseUtils.validateEmail(user.getEmail())){
            throw new InvalidInputParamsException("Email must be a valid email address");
        }
    }

    private void validateUpdatePassword(DtoPassword dtoPassword) {
        if (!bCryptPasswordEncoder.matches(dtoPassword.getPassword(),getCurrentUserPassword())) {
            throw new InvalidInputParamsException("Invalid Current Password");
        }
        validatePassword(dtoPassword.getNewPassword());
        if (!dtoPassword.getConfirmNewPassword().equals(dtoPassword.getNewPassword())) {
            throw new InvalidInputParamsException(ParseUtils.PASSWORDS_NOT_THE_SAME_ERROR);
        }
        if (bCryptPasswordEncoder.matches(dtoPassword.getConfirmNewPassword(),getCurrentUserPassword())) {
            throw new InvalidInputParamsException("New Password cannot be the same as your Current Password");
        }
    }

    private void validatePassword(String password) {
        if (ParseUtils.validatePassword(password)) {
            throw new InvalidInputParamsException(ParseUtils.INVALID_PASSWORD_ERROR);
        }
    }

    private DtoUser toDtoUser(User user) {
        ModelMapper modelMapper = new ModelMapper();
        DtoUser dtoUser = modelMapper.map(user, DtoUser.class);
        dtoUser.setPassword(null);
        dtoUser.setRoles(null);
        return dtoUser;
    }
}