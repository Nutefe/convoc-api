package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.messaging.emails.service.EmailSenderService;
import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Roles;
import com.cyberethik.convocapi.persistance.service.dao.AccountDao;
import com.cyberethik.convocapi.persistance.service.dao.RefreshTokenDao;
import com.cyberethik.convocapi.persistance.service.dao.RoleDao;
import com.cyberethik.convocapi.playload.pages.AccountPage;
import com.cyberethik.convocapi.playload.pages.RolePage;
import com.cyberethik.convocapi.playload.request.AccountRequest;
import com.cyberethik.convocapi.playload.request.IntRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.playload.request.UpdatePassRequest;
import com.cyberethik.convocapi.playload.response.ApiMessage;
import com.cyberethik.convocapi.security.jwt.JwtUtils;
import com.cyberethik.convocapi.security.services.CurrentUser;
import com.cyberethik.convocapi.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cyberethik.convocapi.playload.helper.Helpers.sortByCreatedDesc;

@RestController
@RequestMapping({ "/web/service" })
public class RoleController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_role_page}")
    private String url_role_page;
    @Value("${app.url_role_search_page}")
    private String url_role_search_page;
    @Autowired
    private RoleDao roleDao;
    public RoleController() {
    }
    
    @RequestMapping(value = { "/role/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> roleSave(@Valid @RequestBody final Roles request) throws MessagingException {
        Long id = this.roleDao.count()+1;
        final Roles role = new Roles();
        role.setId(id.intValue());
        role.setLibelle(request.getLibelle());
        role.setDescription(request.getDescription());
        return ResponseEntity.ok(this.roleDao.save(role));
    }
    @RequestMapping(value = { "/role/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> accountUpdate(@Valid @RequestBody final Roles request,
                                         @PathVariable(value = "id") Integer id) throws MessagingException {
        final Roles role = this.roleDao.selectById(id);
        role.setLibelle(request.getLibelle());
        role.setDescription(request.getDescription());
        return ResponseEntity.ok(this.roleDao.save(role));
    }
    
    @RequestMapping(value = { "/role/delete/{id}" }, method = { RequestMethod.DELETE })
    public void accountUpdate(@PathVariable(value = "id") Integer id) throws MessagingException {
        final Roles role = this.roleDao.selectById(id);
        this.roleDao.delete(role);
    }

    @RequestMapping(value = { "/roles/delete" }, method = { RequestMethod.DELETE })
    public void accountDelete(@Valid @RequestBody final IntRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Integer role :
                    request.getIds()) {
                final Roles roleUp = this.roleDao.selectById(role);
                this.roleDao.delete(roleUp);
            }
        }
    }
    @RequestMapping(value = { "/roles" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Roles> selectAll() {
        return this.roleDao.findByDeletedFalseOrderByIdDesc();
    }
    @RequestMapping(value ="/roles/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public RolePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Roles> roles = this.roleDao.findByDeletedFalseOrderByIdDesc(pageable);

        RolePage pages = new RolePage();

        Long total = this.roleDao.countRoles();
        Long lastPage;

        if (total > 0){
            pages.setTotal(total);
            pages.setPer_page(page_size);
            pages.setCurrent_page(page);
            if (total % page_size == 0){
                lastPage = total/page_size;
            } else {
                lastPage = (total/page_size)+1;

            }
            pages.setLast_page(lastPage);
            pages.setFirst_page_url(url_role_page+1);
            pages.setLast_page_url(url_role_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_role_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_role_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(roles);
        } else {
            pages.setTotal(0L);
            pages.setCurrent_page(0);
            pages.setFrom(0L);
            pages.setLast_page(0L);
            pages.setPer_page(0);
            pages.setFrom(0L);
            pages.setFirst_page_url("");
            pages.setNext_page_url("");
            pages.setLast_page_url("");
            pages.setPrev_page_url("");
            pages.setTo(0L);
            pages.setPath(path);
            pages.setData(new ArrayList<>());
        }

        return pages;
    }
    @RequestMapping(value = "/roles/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public RolePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Roles> roles = this.roleDao.recherche(s, pageable);

        RolePage pages = new RolePage();
        Long total = this.roleDao.countRecherche(s);
        Long lastPage;

        if (total > 0){
            pages.setTotal(total);
            pages.setPer_page(page_size);
            pages.setCurrent_page(page);

            if (total %page_size == 0){
                lastPage = total/page_size;
            } else {
                lastPage = (total/page_size)+1;
            }
            pages.setLast_page(lastPage);
            pages.setFirst_page_url(url_role_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_role_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_role_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_role_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(roles);

        }else {
            pages.setTotal(0L);
            pages.setCurrent_page(0);
            pages.setFrom(0L);
            pages.setLast_page(0L);
            pages.setPer_page(0);
            pages.setFrom(0L);
            pages.setFirst_page_url("");
            pages.setNext_page_url("");
            pages.setLast_page_url("");
            pages.setPrev_page_url("");
            pages.setTo(0L);
            pages.setPath(path);
            pages.setData(new ArrayList<>());
        }

        return pages;
    }

}
