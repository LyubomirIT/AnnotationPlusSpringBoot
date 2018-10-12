package com.nbu.annotationplus.service;

import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.model.Role;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.RoleRepository;
import com.nbu.annotationplus.repository.UserRepository;
import com.nbu.annotationplus.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public User updateUser(User userDetails){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        currentUser.setName(userDetails.getName());
        currentUser.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        currentUser.setLastName(userDetails.getLastName());
        User updatedUser = userRepository.save(currentUser);
        return updatedUser;
    }

    public User getCurrentUser(){
        Authentication authentication = validateUser();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail);
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

}