package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoForgotPassword;
import com.nbu.annotationplus.dto.DtoMail;
import com.nbu.annotationplus.dto.DtoPassword;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.persistence.entity.PasswordResetToken;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.PasswordResetTokenRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import com.nbu.annotationplus.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PasswordService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

   @Transactional
   public ResponseEntity<?> resetPassword(DtoPassword dtoPassword){
       PasswordResetToken resetToken = tokenRepository.findByToken(dtoPassword.getToken());
       if(resetToken == null){
           throw new InvalidInputParamsException("Invalid Token");
       }
       if (resetToken.isExpired()){
           throw new InvalidInputParamsException("Token is Expired");
       }
       validateResetPassword(dtoPassword);
       User user = resetToken.getUser();
       String updatedPassword = passwordEncoder.encode(dtoPassword.getConfirmNewPassword());
       updatePassword(updatedPassword, user.getId());
       tokenRepository.delete(resetToken);

       return new ResponseEntity<>(HttpStatus.OK);
   }

    @Transactional
    public ResponseEntity<?> requestToken(DtoForgotPassword dtoForgotPassword, HttpServletRequest request){
        UserUtils.validateEmail(dtoForgotPassword.getEmail());
        User user = userRepository.findByEmail(dtoForgotPassword.getEmail());
        if (user == null){
            throw new InvalidInputParamsException("User does not exist");
        }

        Long userId = user.getId();

        PasswordResetToken token = tokenRepository.findByUserId(userId);

        if(token != null){
            token.setToken(UUID.randomUUID().toString());
            token.setExpiryDate(30);
            tokenRepository.save(token);
        } else {
            token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString());
            token.setUser(user);
            token.setExpiryDate(30);
            tokenRepository.save(token);
        }

        DtoMail dtoMail = new DtoMail();
        dtoMail.setFrom("annotationplus@gmail.com");
        dtoMail.setTo(user.getEmail());
        dtoMail.setSubject("Password reset request");

        Map<String, Object> model = new HashMap<>();
        model.put("token", token);
        model.put("user", user);
        model.put("signature", "https://annotationplus.com");
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
        dtoMail.setModel(model);
        emailService.sendEmail(dtoMail);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateResetPassword(DtoPassword dtoPassword) {
        UserUtils.validatePassword(dtoPassword.getNewPassword());
        if (!dtoPassword.getConfirmNewPassword().equals(dtoPassword.getNewPassword())) {
            throw new InvalidInputParamsException(UserUtils.PASSWORDS_NOT_THE_SAME_ERROR);
        }
    }

    private void updatePassword(String password, Long userId){
        userRepository.updatePassword(password, userId);
    }
}
