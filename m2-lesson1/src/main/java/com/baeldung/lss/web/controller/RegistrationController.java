package com.baeldung.lss.web.controller;


import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;

    @Autowired
    public RegistrationController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "user", new User());
    }

    @RequestMapping("user/register")
    public ModelAndView registerUser(@Valid final  User user, final BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "user", new User());
        }

        userRepository.save(user);
        return new ModelAndView("redirect:/login");
    }
}
