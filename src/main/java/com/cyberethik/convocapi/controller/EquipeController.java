package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.EquipeRequest;
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
public class EquipeController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_equipe_page}")
    private String url_equipe_page;
    @Value("${app.url_equipe_search_page}")
    private String url_equipe_search_page;
    @Value("${app.url_equipe_org_page}")
    private String url_equipe_org_page;
    @Value("${app.url_equipe_org_search_page}")
    private String url_equipe_org_search_page;
    @Autowired
    private EquipeDao equipeDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOrganisationDao accountOrganisationDao;
    @Autowired
    private EquipeMembreDao equipeMembreDao;
    @Autowired
    private OrganisationDao organisationDao;
    @Autowired
    private EvenementEquipeDao evenementEquipeDao;
    @Autowired
    private EvenementDao evenementDao;
    public EquipeController() {
    }
    
    @RequestMapping(value = { "/equipe/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> equipeSave(@Valid @RequestBody final EquipeRequest request) throws MessagingException {
        Equipes equipe = new Equipes();
        equipe.setLibelle(request.getLibelle());
        equipe.setDescription(request.getDescription());
        equipe.setDateFin(request.getDateFin());
        equipe.setActif(true);
        equipe.setOrganisation(this.organisationDao.selectById(request.getOrganisation().getId()));
        return ResponseEntity.ok(this.equipeDao.save(equipe));
    }
    @RequestMapping(value = { "/equipe/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> equipeUpdate(@Valid @RequestBody final EquipeRequest request,
                                         @PathVariable(value = "id") Long id) throws MessagingException {
        Equipes equipe = this.equipeDao.selectById(id);
        equipe.setLibelle(request.getLibelle());
        equipe.setDescription(request.getDescription());
        equipe.setDateFin(request.getDateFin());
        equipe.setActif(true);
        equipe.setOrganisation(this.organisationDao.selectById(request.getOrganisation().getId()));
        return ResponseEntity.ok(this.equipeDao.save(equipe));
    }
    
    @RequestMapping(value = { "/equipe/delete/{id}" }, method = { RequestMethod.DELETE })
    public void equipeUpdate(@PathVariable(value = "id") Long id) throws MessagingException {
        final Equipes equipe = this.equipeDao.selectById(id);
        this.equipeDao.delete(equipe);
    }

    @RequestMapping(value = { "/equipes/delete" }, method = { RequestMethod.DELETE })
    public void equipeDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long id :
                    request.getIds()) {
                Equipes equipe = this.equipeDao.selectById(id);
                this.equipeDao.delete(equipe);
            }
        }
    }
    @RequestMapping(value = { "/equipes" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Equipes> selectAll() {
        return this.equipeDao.findByDeletedFalseOrderByIdDesc();
    }
    @RequestMapping(value = { "/equipes/organisation" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Equipes> selectAll(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        return this.equipeDao.seletByOrganisation(ids);
    }
    @RequestMapping(value = { "/equipes/evenement/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Equipes> selectAll(@PathVariable(value = "id") Long id) {
        final Evenements evenement = this.evenementDao.selectById(id);
        return this.evenementEquipeDao.selectByEvenement(evenement);
    }
    @RequestMapping(value ="/equipes/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Equipes> equipes = this.equipeDao.findByDeletedFalseOrderByIdDesc(pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.equipeDao.countEquipes();
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
            pages.setFirst_page_url(url_equipe_page+1);
            pages.setLast_page_url(url_equipe_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_equipe_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_equipe_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(equipes);
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
    @RequestMapping(value = "/equipes/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Equipes> equipes = this.equipeDao.recherche(s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.equipeDao.countRecherche(s);
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
            pages.setFirst_page_url(url_equipe_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_equipe_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_equipe_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_equipe_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(equipes);

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
    @RequestMapping(value ="/equipes/organisation/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @CurrentUser final UserDetailsImpl currentUser){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Equipes> equipes = this.equipeDao.seletByOrganisation(ids, pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.equipeDao.countOrganisation(ids);
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
            pages.setFirst_page_url(url_equipe_org_page+1);
            pages.setLast_page_url(url_equipe_org_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_equipe_org_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_equipe_org_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(equipes);
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
    @RequestMapping(value = "/equipes/organisation/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(name="s") String s,
                                   @CurrentUser final UserDetailsImpl currentUser){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Equipes> equipes = this.equipeDao.recherche(ids, s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.equipeDao.countRecherche(ids, s);
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
            pages.setFirst_page_url(url_equipe_org_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_equipe_org_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_equipe_org_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_equipe_org_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(equipes);

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
