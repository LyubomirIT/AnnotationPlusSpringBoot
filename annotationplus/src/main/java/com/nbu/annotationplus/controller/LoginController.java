package com.nbu.annotationplus.controller;

import javax.validation.Valid;

import com.nbu.annotationplus.dto.DtoNote;
import com.nbu.annotationplus.dto.DtoUser;
import com.nbu.annotationplus.persistence.entity.Note;
import com.nbu.annotationplus.persistence.entity.PasswordResetToken;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.NoteRepository;
import com.nbu.annotationplus.persistence.repository.PasswordResetTokenRepository;
import com.nbu.annotationplus.service.NoteService;
import com.nbu.annotationplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private NoteService noteService;

    @RequestMapping(value= "/login", method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value={"/", "/landing"}, method = RequestMethod.GET)
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("landing");
        return modelAndView;
    }


    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        //User user = new User();
        DtoUser dtoUser = new DtoUser();
        modelAndView.addObject("dtoUser", dtoUser);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    /*@RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if(user.getEmail() == null){
            bindingResult
                    .rejectValue("email", "error.user",
                            "Please provide a valid Email");
        }
        if(user.getEmail().trim().equals("")){
            bindingResult
                    .rejectValue("email", "error.user",
                            "Please provide a valid Email");
        }
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
           // modelAndView.setViewName("registration");
            modelAndView.setViewName("login");

        }
        return modelAndView;
    }*/

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid DtoUser dtoUser, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        try{
            userService.saveUser(dtoUser);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("dtoUser", new DtoUser());
            modelAndView.setViewName("login");
            //modelAndView.l
        }catch (RuntimeException e){
           // modelAndView.addObject("error", e.getMessage());
            bindingResult//.reject("error.user",
                    .rejectValue("email", "error.dtoUser",
                            e.getMessage());
        }
        return modelAndView;
    }

    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
    public ModelAndView adminHome(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName",  user.getName() + " " + user.getLastName());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value="/admin/profile", method = RequestMethod.GET)
    public ModelAndView profile(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName",  user.getName() + " " + user.getLastName());
        modelAndView.setViewName("admin/profile");
        return modelAndView;
    }

    @RequestMapping(value="/admin/feedback", method = RequestMethod.GET)
    public ModelAndView feedback(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName",  user.getName()+ " " + user.getLastName());
        modelAndView.addObject("email",  auth.getName());
        modelAndView.setViewName("admin/feedback");
        return modelAndView;
    }

    @RequestMapping(value="/reset-password", method = RequestMethod.GET)
    public ModelAndView displayResetPasswordPage(@RequestParam(required = false) String token) {
        ModelAndView modelAndView = new ModelAndView();
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null) {
            modelAndView.addObject("error", "Could not find password reset token.");
        } else if (resetToken.isExpired()) {
            modelAndView.addObject("error", "Token has expired, please request a new password reset.");
        } else {
            modelAndView.addObject("token", resetToken.getToken());
        }
        return modelAndView;
    }

    @RequestMapping(value="/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPassword(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("forgot-password");
        return modelAndView;
    }

    @RequestMapping(value="/admin/source", method = RequestMethod.GET)
    public ModelAndView source(@RequestParam(required = false) Long id){
        DtoNote dtoNote = noteService.getNoteById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/source");
        modelAndView.addObject("sourceContent", dtoNote.getContent());
        modelAndView.addObject("sourceName", dtoNote.getTitle());
        return modelAndView;
    }

    /*@RequestMapping(value="/admin/viewer", method = RequestMethod.GET)
    public ModelAndView viewer(@RequestParam(required = false) Long id){
        Note note = noteRepository.findNoteById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/viewer");
        modelAndView.addObject("noteContent", note.getContent());
        modelAndView.addObject("noteName", note.getTitle());
        return modelAndView;
    }*/
}
