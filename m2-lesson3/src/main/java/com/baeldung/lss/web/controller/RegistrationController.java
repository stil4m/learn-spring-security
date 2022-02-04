package com.baeldung.lss.web.controller;

import com.baeldung.lss.persistence.VerificationTokenRepository;
import com.baeldung.lss.web.model.VerificationToken;
import com.baeldung.lss.service.IUserService;
import com.baeldung.lss.validation.EmailExistsException;
import com.baeldung.lss.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@Controller
class RegistrationController {

    @Autowired
    private IUserService userService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    //

    @RequestMapping(value = "signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "user", new User());
    }

    @RequestMapping(value = "user/register")
    public ModelAndView registerUser(@Valid final User user, final BindingResult result, final HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "user", user);
        }
        try {
            user.setEnabled(false);
            userService.registerNewUser(user);

            final String token = UUID.randomUUID().toString();
            final VerificationToken verificationToken = new VerificationToken(token, user);
            verificationTokenRepository.save(verificationToken);

            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            sendVerification(user, token, appUrl);

        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "user", user);
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final Model model, @RequestParam("token") String token, final RedirectAttributes redirectAttributes) throws EmailExistsException {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        final User user = verificationToken.getUser();

        System.out.println(user);

        user.setEnabled(true);
        userService.updateExistingUser(user);

        redirectAttributes.addFlashAttribute("message", "Your account verified successfully");
        return new ModelAndView("redirect:/login");

    }

    private void sendVerification(final User user, final String token, final String appUrl) {
        System.out.println();
        System.out.println("\t" + appUrl + "/registrationConfirm?token=" + token);
        System.out.println();
    }

}
