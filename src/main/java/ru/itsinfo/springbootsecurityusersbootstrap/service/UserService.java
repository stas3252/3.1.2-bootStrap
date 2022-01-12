package ru.itsinfo.springbootsecurityusersbootstrap.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itsinfo.springbootsecurityusersbootstrap.config.exception.LoginException;
import ru.itsinfo.springbootsecurityusersbootstrap.model.User;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService extends UserDetailsService {

    void authenticateOrLogout(Model model, HttpSession session, LoginException authenticationException, String authenticationName);

    List<User> findAllUsers();

    User findUser(Long userId);

    void deleteUser(Long userId);

    void insertUser(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes);

    void updateUser(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes);
}
