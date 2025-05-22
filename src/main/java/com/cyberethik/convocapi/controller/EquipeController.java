package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.persistance.dto.MembreDto;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.persistance.utils.UtilMembre;
import com.cyberethik.convocapi.playload.helper.Helpers;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.EquipeRequest;
import com.cyberethik.convocapi.playload.request.FilterRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.playload.request.MembreRequest;
import com.cyberethik.convocapi.playload.response.ApiMessage;
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
import java.util.Date;
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
    @Autowired
    private MembreDao membreDao;
    public EquipeController() {
    }
    
    @RequestMapping(value = { "/equipe/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> equipeSave(@Valid @RequestBody final EquipeRequest request,
                                        @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);

        Organisations organisationInit = this.organisationDao.selectById(organisations.get(0).getId());
        if (organisationInit != null){
            if (organisationInit.getEquipeMax() == this.equipeDao.countByOrganisation(organisationInit.getId())){
                return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'équipe atteint", "Access denied"));
            }
        }

        Equipes equipe = new Equipes();
        equipe.setLibelle(request.getLibelle());
        equipe.setDescription(request.getDescription());
        equipe.setDateFin(request.getDateFin());
        equipe.setActif(true);
        equipe.setOrganisation(organisationInit);
        Equipes equipeSave = this.equipeDao.save(equipe);
        if (equipeSave!=null){
            if (request.getMembres() != null){
                if (request.getMembres().size()>0){
                    for (Membres eq :
                            request.getMembres()) {
                        Membres membre = this.membreDao.selectById(eq.getId());
                        if (Helpers.compareDate(membre.getDateFin(), new Date())){
                            if (equipe.getOrganisation().getMembreEquActifs() == this.equipeMembreDao.countByEquipe(equipe, new Date())){
                                return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'membres actifs atteint pour l'équipe", "Access denied"));
                            }
                        }

                        EquipeMembres equipeMembre = new EquipeMembres();
                        EquipeMembrePK pk = new EquipeMembrePK(equipe, membre);
                        equipeMembre.setId(pk);
                        this.equipeMembreDao.save(equipeMembre);
                    }
                }
            }

        }

        return ResponseEntity.ok(equipeSave);
    }
    @RequestMapping(value = { "/equipe/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> equipeUpdate(@Valid @RequestBody final EquipeRequest request,
                                         @PathVariable(value = "id") Long id,
                                          @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {

        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);

        Organisations organisationInit = this.organisationDao.selectById(organisations.get(0).getId());

        Equipes equipe = this.equipeDao.selectById(id);
        equipe.setLibelle(request.getLibelle());
        equipe.setDescription(request.getDescription());
        equipe.setDateFin(request.getDateFin());
        equipe.setActif(true);
        equipe.setOrganisation(organisationInit);
        Equipes equipeSave = this.equipeDao.save(equipe);
        if (equipeSave!=null){
            if (request.getMembres() != null){
                if (request.getMembres().size()>0){
                    for (Membres eq :
                            request.getMembres()) {
                        Membres membre = this.membreDao.selectById(eq.getId());
                        if (Helpers.compareDate(membre.getDateFin(), new Date())){
                            if (equipe.getOrganisation().getMembreEquActifs() == this.equipeMembreDao.countByEquipe(equipe, new Date())){
                                return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'membres actifs atteint pour l'équipe", "Access denied"));
                            }
                        }

                        EquipeMembres equipeMembre = new EquipeMembres();
                        EquipeMembrePK pk = new EquipeMembrePK(equipe, membre);
                        equipeMembre.setId(pk);
                        this.equipeMembreDao.save(equipeMembre);
                    }
                }
            }
        }

        return ResponseEntity.ok(equipeSave);
    }

    @RequestMapping(value = { "/equipe/add/membre/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> membreUpdate(@Valid @RequestBody final LongRequest request,
                                          @PathVariable(value = "id") Long id,
                                          @CurrentUser final UserDetailsImpl currentUser)
            throws MessagingException {
        Equipes equipe = this.equipeDao.selectById(id);

        if (equipe!=null){
            if (request.getIds().size()>0){
                for (Long eq :
                        request.getIds()) {
                    Membres membre = this.membreDao.selectById(eq);
                    if (Helpers.compareDate(membre.getDateFin(), new Date())){
                        if (equipe.getOrganisation().getMembreEquActifs() == this.equipeMembreDao.countByEquipe(equipe, new Date())){
                            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'membres actifs atteint pour l'équipe", "Access denied"));
                        }
                    }

                    EquipeMembres equipeMembre = new EquipeMembres();
                    EquipeMembrePK pk = new EquipeMembrePK(equipe, membre);
                    equipeMembre.setId(pk);
                    this.equipeMembreDao.save(equipeMembre);
                }
            }
        }
        return ResponseEntity.ok(new ApiMessage(HttpStatus.OK, "Membre ajouter avec succes"));
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

    @RequestMapping(value = { "/equipes/membre/delete/{id}" }, method = { RequestMethod.DELETE })
    public void equipeDelete(@PathVariable(value = "id") Long id,
                             @Valid @RequestBody final LongRequest request) {
        Equipes equipe = this.equipeDao.selectById(id);
        if (equipe!=null){
            if (request.getIds().size()>0){
                for (Long eq :
                        request.getIds()) {
                    Membres membre = this.membreDao.selectById(eq);
                    EquipeMembrePK pk = new EquipeMembrePK(equipe, membre);
                    EquipeMembres equipeMembre = this.equipeMembreDao.selectById(pk);
//                    equipeMembre.setId(pk);
                    this.equipeMembreDao.delete(equipeMembre);
                }
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
    @RequestMapping(value = { "/equipes/membre/actif/not/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Equipes> selectAllActifNot(@PathVariable(value = "id") Long id,
                                             @CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        final Membres membre = this.membreDao.selectById(id);
        List<Equipes> equipes = this.equipeDao.selectOrganisation(ids, membre, new Date());
        return equipes;
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
    @RequestMapping(value ="/equipes/membre/filter/page/{page}", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePage selectPageFilter(@PathVariable(value = "page") int page,
                                         @Valid @RequestBody final FilterRequest request,
                                         @CurrentUser final UserDetailsImpl currentUser) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Equipes> equipes = new ArrayList<>();
        Long total = 0L;
        if (request.getEtats().size() == 2){
            if (request.getNoms().size() > 0){
                equipes = this.equipeDao.recherche(ids, request.getNoms(), pageable);
                total = this.equipeDao.countRecherche(ids, request.getNoms());
            } else {
                equipes = this.equipeDao.recherche(ids, pageable);
                total = this.equipeDao.countRecherche(ids);
            }
        } else if (request.getEtats().size() == 0) {
            equipes = this.equipeDao.recherche(ids, request.getNoms(), pageable);
            total = this.equipeDao.countRecherche(ids, request.getNoms());
        } else {
            if (request.getEtats().size() == 1){
                Boolean etat = request.getEtats().get(0);
                if (etat==true){
                    equipes = this.equipeDao.rechercheActif(ids, request.getNoms(), new Date(), pageable);
                    total = this.equipeDao.countRechercheActif(ids, request.getNoms(), new Date());
                }else {
                    equipes = this.equipeDao.rechercheInactif(ids, request.getNoms(), new Date(), pageable);
                    total = this.equipeDao.countRechercheInactif(ids, request.getNoms(), new Date());
                }
            }
        }

        ResponsePage pages = new ResponsePage();

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
}
