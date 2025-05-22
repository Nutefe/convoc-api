package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.cyberethik.convocapi.persistance.entities.Roles;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.pages.RolePage;
import com.cyberethik.convocapi.playload.request.IntRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.security.services.CurrentUser;
import com.cyberethik.convocapi.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.cyberethik.convocapi.playload.helper.Helpers.sortByCreatedDesc;

@RestController
@RequestMapping({ "/web/service" })
public class ResponsableController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_responsable_page}")
    private String url_responsable_page;
    @Value("${app.url_responsable_search_page}")
    private String url_responsable_search_page;
    @Value("${app.url_responsable_org_page}")
    private String url_responsable_org_page;
    @Value("${app.url_responsable_org_search_page}")
    private String url_responsable_org_search_page;
    @Autowired
    private ResponsableDao responsableDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOrganisationDao accountOrganisationDao;
    @Autowired
    private EquipeMembreDao equipeMembreDao;
    public ResponsableController() {
    }
    
    @RequestMapping(value = { "/responsable/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> responsableSave(@Valid @RequestBody final Responsables request,
                                             @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {

        if (responsableDao.existsByEmail(request.getEmail())){
            return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error email already exists", "Error duplication key"));
        }
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
        Responsables responsable = new Responsables();
        responsable.setLibelle(request.getLibelle());
        responsable.setEmail(request.getEmail());
        responsable.setTelephone(request.getTelephone());
        responsable.setAdresse(request.getAdresse());
        if (organisations.size()>0)
            responsable.setOrganisation(organisations.get(0));
        return ResponseEntity.ok(this.responsableDao.save(responsable));
    }
    @RequestMapping(value = { "/responsable/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> responsableUpdate(@Valid @RequestBody final Responsables request,
                                         @PathVariable(value = "id") Long id,
                                               @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {
        if (responsableDao.existsByEmail(request.getEmail(), id)){
            return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error email already exists", "Error duplication key"));
        }
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
        Responsables responsable = this.responsableDao.selectById(id);
        responsable.setLibelle(request.getLibelle());
        responsable.setEmail(request.getEmail());
        responsable.setTelephone(request.getTelephone());
        responsable.setAdresse(request.getAdresse());
        if (organisations.size()>0)
            responsable.setOrganisation(organisations.get(0));
        return ResponseEntity.ok(this.responsableDao.save(responsable));
    }


    @RequestMapping(value = { "/responsable/check/email/exist" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkEmail(@RequestParam(name="email") String email) {
        return this.responsableDao.existsByEmail(email);
    }
    @RequestMapping(value = { "/responsable/check/email/exist/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkEmail(@RequestParam(name="email") String email,
                              @PathVariable(value = "id") Long id) {
        return this.responsableDao.existsByEmail(email, id);
    }
    
    @RequestMapping(value = { "/responsable/delete/{id}" }, method = { RequestMethod.DELETE })
    public void responsableUpdate(@PathVariable(value = "id") Long id) throws MessagingException {
        final Responsables responsable = this.responsableDao.selectById(id);
        this.responsableDao.delete(responsable);
    }

    @RequestMapping(value = { "/responsables/delete" }, method = { RequestMethod.DELETE })
    public void responsableDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long id :
                    request.getIds()) {
                Responsables responsable = this.responsableDao.selectById(id);
                this.responsableDao.delete(responsable);
            }
        }
    }
    @RequestMapping(value = { "/responsables" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Responsables> selectAll() {
        return this.responsableDao.findByDeletedFalseOrderByIdDesc();
    }
    @RequestMapping(value = { "/responsables/organisation" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Responsables> selectAll(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        return this.responsableDao.seletByOrganisation(ids);
    }
    @RequestMapping(value ="/responsables/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Responsables> responsables = this.responsableDao.findByDeletedFalseOrderByIdDesc(pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.responsableDao.countResponsables();
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
            pages.setFirst_page_url(url_responsable_page+1);
            pages.setLast_page_url(url_responsable_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_responsable_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_responsable_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(responsables);
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
    @RequestMapping(value = "/responsables/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Responsables> responsables = this.responsableDao.recherche(s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.responsableDao.countRecherche(s);
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
            pages.setFirst_page_url(url_responsable_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_responsable_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_responsable_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_responsable_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(responsables);

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
    @RequestMapping(value ="/responsables/organisation/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @CurrentUser final UserDetailsImpl currentUser){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Responsables> responsables = this.responsableDao.seletByOrganisation(ids, pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.responsableDao.countOrganisation(ids);
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
            pages.setFirst_page_url(url_responsable_org_page+1);
            pages.setLast_page_url(url_responsable_org_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_responsable_org_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_responsable_org_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(responsables);
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
    @RequestMapping(value = "/responsables/organisation/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(name="s") String s,
                                   @CurrentUser final UserDetailsImpl currentUser){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Responsables> responsables = this.responsableDao.recherche(ids, s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.responsableDao.countRecherche(ids, s);
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
            pages.setFirst_page_url(url_responsable_org_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_responsable_org_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_responsable_org_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_responsable_org_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(responsables);

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
