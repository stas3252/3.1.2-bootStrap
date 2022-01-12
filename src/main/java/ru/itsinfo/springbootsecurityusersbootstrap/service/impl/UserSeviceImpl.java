package ru.itsinfo.springbootsecurityusersbootstrap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itsinfo.springbootsecurityusersbootstrap.config.exception.LoginException;
import ru.itsinfo.springbootsecurityusersbootstrap.model.User;
import ru.itsinfo.springbootsecurityusersbootstrap.repository.UserRepository;
import ru.itsinfo.springbootsecurityusersbootstrap.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Transactional
public class UserSeviceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserSeviceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username %s not found", email))
        );
    }

    @Override
    public void authenticateOrLogout(Model model, HttpSession session, LoginException authenticationException, String authenticationName) {

        if (authenticationException != null) { // Восстанавливаем неверно введенные данные
            model.addAttribute("authenticationException", authenticationException);
            session.removeAttribute("Authentication-Exception");
        } else {
            model.addAttribute("authenticationException", new LoginException(null));
        }

        if (authenticationName != null) { // Выводим прощальное сообщение
            model.addAttribute("authenticationName", authenticationName);
            session.removeAttribute("Authentication-Name");
        }
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "firstName", "lastName"));
    }

    @Override
    public User findUser(Long userId) throws IllegalArgumentException {
        return userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with ID %d not found", userId)));
    }

    @Override
    public void insertUser(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } else {
            addRedirectAttributesIfErrorsExists(user, bindingResult, redirectAttributes);
        }
    }

    @Override
    public void updateUser(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        bindingResult = checkBindingResultForPasswordField(bindingResult);

        if (!bindingResult.hasErrors()) {
            user.setPassword(user.getPassword().isEmpty() ? // todo если нет такого юзера try
                    findUser(user.getId()).getPassword() :
                    passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } else {
            addRedirectAttributesIfErrorsExists(user, bindingResult, redirectAttributes);
        }
    }
    private void addErrorIfDataIntegrityViolationException(BindingResult bindingResult) {
        bindingResult.addError(new FieldError(bindingResult.getObjectName(),
                "email", "E-mail must be unique"));
    }

    private void addRedirectAttributesIfErrorsExists(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
    }

    /**
     * Удаляет ошибку, если у существующего User пустое поле password
     * @param bindingResult BeanPropertyBindingResult
     * @return BeanPropertyBindingResult
     */
   private BindingResult checkBindingResultForPasswordField(BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors()) {
            return bindingResult;
        }

        User user = (User) bindingResult.getTarget();
        BindingResult newBindingResult = new BeanPropertyBindingResult(user, bindingResult.getObjectName());
        for (FieldError error : bindingResult.getFieldErrors()) {
            if (!user.isNew() && !error.getField().equals("password")) {
                newBindingResult.addError(error);
            }
        }

        return newBindingResult;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
