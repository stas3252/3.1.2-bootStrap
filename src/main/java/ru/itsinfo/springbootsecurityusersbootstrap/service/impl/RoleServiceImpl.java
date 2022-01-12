package ru.itsinfo.springbootsecurityusersbootstrap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itsinfo.springbootsecurityusersbootstrap.model.Role;
import ru.itsinfo.springbootsecurityusersbootstrap.repository.RoleRepository;
import ru.itsinfo.springbootsecurityusersbootstrap.service.RoleService;


@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    @Override
    public Iterable<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}
