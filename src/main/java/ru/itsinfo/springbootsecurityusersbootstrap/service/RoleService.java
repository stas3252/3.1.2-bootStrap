package ru.itsinfo.springbootsecurityusersbootstrap.service;

import ru.itsinfo.springbootsecurityusersbootstrap.model.Role;

public interface RoleService {
    Iterable<Role> findAllRoles();
}
