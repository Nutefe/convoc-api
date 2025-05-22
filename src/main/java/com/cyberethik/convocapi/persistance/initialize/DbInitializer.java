package com.cyberethik.convocapi.persistance.initialize;


import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Roles;
import com.cyberethik.convocapi.persistance.repository.AccountRepository;
import com.cyberethik.convocapi.persistance.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnProperty(name = { "app.db-init" }, havingValue = "true")
public class DbInitializer implements CommandLineRunner
{
    private AccountRepository accountRepository;
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DbInitializer(AccountRepository accountRepository,
                         RoleRepository roleRepository
                         ) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;

    }

    public void run(final String... args) throws Exception {

        Roles admin = new Roles();
        admin.setId(1);
        admin.setLibelle("ROLE_ADMIN");

        if (!this.roleRepository.existsById(1)){
            this.roleRepository.save(admin);
        }

        Roles pro = new Roles();
        pro.setId(2);
        pro.setLibelle("ROLE_MEMBRE");

        if (!this.roleRepository.existsById(2)){
            this.roleRepository.save(pro);
        }

        final Accounts user = new Accounts();
        user.setActif(true);
        user.setActifAt(new Date());
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(new Date());
        user.setEmail("sysadmin@gmail.com");
        user.setLibelle("sysadmin");
        user.setRole(admin);
        user.setPassword(this.passwordEncoder.encode((CharSequence)"#1234#Sys"));
        if (!this.accountRepository.existsByEmail("sysadmin@gmail.com")) {
            this.accountRepository.save(user);
        }

        System.out.println(" -- Database has been initialized");

    }
}
