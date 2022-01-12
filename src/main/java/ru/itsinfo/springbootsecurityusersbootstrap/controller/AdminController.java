package ru.itsinfo.springbootsecurityusersbootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itsinfo.springbootsecurityusersbootstrap.model.User;
import ru.itsinfo.springbootsecurityusersbootstrap.service.RoleService;
import ru.itsinfo.springbootsecurityusersbootstrap.service.UserService;

import javax.validation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public AdminController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping({"", "list"})
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("allRoles", roleService.findAllRoles());

        model.addAttribute("showUserProfile",
                model.containsAttribute("user") && !((User) model.getAttribute("user")).isNew());
        model.addAttribute("showNewUserForm",
                model.containsAttribute("user") && ((User) model.getAttribute("user")).isNew());
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }

        return "admin-page";
    }

    @GetMapping("/{id}/profile")
    public String showUserProfileModal(@PathVariable("id") Long userId, Model model, RedirectAttributes attributes) {
        try {
            model.addAttribute("allRoles", roleService.findAllRoles());
            model.addAttribute("user", userService.findUser(userId));
            return "fragments/user-form";
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping()
    public String updateUser(@Valid @ModelAttribute("user") User user,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        userService.updateUser(user, bindingResult, redirectAttributes);

        return "redirect:/admin";
    }

    @DeleteMapping("")
    public String deleteUser(@ModelAttribute("user") User user) {
        userService.deleteUser(user.getId());
        return "redirect:/admin";
    }

    @PostMapping()
    public String insertUser(@Valid @ModelAttribute("user") User user,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        userService.insertUser(user, bindingResult, redirectAttributes);

        return "redirect:/admin";
    }
}
