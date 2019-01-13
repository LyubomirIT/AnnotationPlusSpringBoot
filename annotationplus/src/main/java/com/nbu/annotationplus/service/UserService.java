package com.nbu.annotationplus.service;

import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.model.Note;
import com.nbu.annotationplus.model.Role;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.model.userDto;
import com.nbu.annotationplus.repository.RoleRepository;
import com.nbu.annotationplus.repository.UserRepository;
import com.nbu.annotationplus.utils.AuthUtils;
import com.nbu.annotationplus.utils.ParseUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;

@Service("userService")
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
    public User saveUser(User user) {
        validateFirstAndLastName(user);
        validateEmail(user);
        validatePassword(user);
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null){
            throw new InvalidInputParamsException("User with that email already exists");
        }
        //validatePassword(user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        //System.out.println(userRepository.save(user).notify("ds"));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        validateFirstAndLastName(user);
        currentUser.setName(user.getName());
        currentUser.setLastName(user.getLastName());
        return userRepository.save(currentUser);
    }

    //private StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();

    @Transactional
    public ResponseEntity<?> updatePassword (userDto userDto){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        System.out.println("Current Password:" + currentUser.getPassword());
        System.out.println("User Dto Password:" + bCryptPasswordEncoder.encode(userDto.getPassword()));
        validateUpdatePassword(userDto);
        currentUser.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userRepository.save(currentUser);
        return ResponseEntity.ok().build();
    }



    public User getCurrentUser(){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void updatePassword(String password, Long userId){
        userRepository.updatePassword(password, userId);
    }

    private Authentication validateUser() {
        Authentication authentication = AuthUtils.getAuthenticateduser();
        if (authentication == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        else {
            return authentication;
        }
    }

    private void validateFirstAndLastName(User user){
        if(ParseUtils.validateTitle(user.getName())){
            throw new InvalidInputParamsException("Invalid First Name");
        }
        if(ParseUtils.validateTitle(user.getLastName())){
            throw new InvalidInputParamsException("Invalid Last Name");
        }
    }

    private void validateEmail(User user){
        if(!ParseUtils.validateEmail(user.getEmail())){
            throw new InvalidInputParamsException("Email must be a valid email address");
        }
    }

    private void validateUpdatePassword(userDto userDto) {
        if (ParseUtils.validatePassword(userDto.getPassword())) {
            throw new InvalidInputParamsException("Invalid Current Password");
        }
        if (ParseUtils.validatePassword(userDto.getNewPassword())) {
            throw new InvalidInputParamsException("Invalid New Password");
        }
        if (!userDto.getConfirmNewPassword().equals(userDto.getNewPassword())) {
            throw new InvalidInputParamsException("New Password must have the same value as Confirm password !");
        }
    }

    private void validatePassword(User user) {
        if (ParseUtils.validatePassword(user.getPassword())) {
            throw new InvalidInputParamsException("Invalid Password");
        }
    }

    /*private userDto toDtoUser(User user) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(user, userDto.class);
    }*/
}