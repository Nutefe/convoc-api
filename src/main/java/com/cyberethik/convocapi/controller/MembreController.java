package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.persistance.dto.AccountDto;
import com.cyberethik.convocapi.persistance.dto.EquipeDto;
import com.cyberethik.convocapi.persistance.dto.MembreDto;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.persistance.utils.UtilAccount;
import com.cyberethik.convocapi.persistance.utils.UtilEquipe;
import com.cyberethik.convocapi.persistance.utils.UtilMembre;
import com.cyberethik.convocapi.playload.helper.Helpers;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.FilterRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.playload.request.MembreRequest;
import com.cyberethik.convocapi.playload.response.ApiMessage;
import com.cyberethik.convocapi.security.services.CurrentUser;
import com.cyberethik.convocapi.security.services.UserDetailsImpl;
import org.apache.commons.lang3.RandomStringUtils;
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
public class MembreController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_membre_page}")
    private String url_membre_page;
    @Value("${app.url_membre_search_page}")
    private String url_membre_search_page;
    @Value("${app.url_membre_org_page}")
    private String url_membre_org_page;
    @Value("${app.url_membre_org_search_page}")
    private String url_membre_org_search_page;
    @Value("${app.url_membre_eq_page}")
    private String url_membre_eq_page;
    @Value("${app.url_membre_eq_search_page}")
    private String url_membre_eq_search_page;
    @Autowired
    private MembreDao membreDao;
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
    private ResponsableDao responsableDao;
    @Autowired
    private EvenementDao evenementDao;
    @Autowired
    private EvenementEquipeDao evenementEquipeDao;
    public MembreController() {
    }
    
    @RequestMapping(value = { "/membre/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> membreSave(@Valid @RequestBody final MembreRequest request,
                                        @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {
        if (!request.getEmail().isBlank()){
            if (membreDao.existsByEmail(request.getEmail())){
                return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error email already exists", "Error duplication key"));
            }
        }

        Membres membre = new Membres();
        String slug;
        do {
            int length = 10;
            boolean useLetters = true;
            boolean useNumbers = true;
            slug = RandomStringUtils.random(length, useLetters, useNumbers);
        }while (this.membreDao.existsBySlug(slug));
        if (!request.isResponsable()){
            if (request.getEmailResponsable() != null){
                if (this.responsableDao.existsByEmail(request.getEmailResponsable())){
                    Responsables responsableSave = this.responsableDao.selectByEmail(request.getEmailResponsable());
                    membre.setResponsable(responsableSave);
                    membre.setOrganisation(responsableSave.getOrganisation());
                } else {
                    final Accounts account = this.accountDao.selectById(currentUser.getId());
                    List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
                    Responsables responsable = new Responsables();
                    responsable.setLibelle(request.getLibelleResponsable());
                    responsable.setEmail(request.getEmailResponsable());
                    responsable.setTelephone(request.getTelephoneResponsable());
                    responsable.setAdresse(request.getAdresseResponsable());
                    if (organisations.size()>0)
                        responsable.setOrganisation(organisations.get(0));

                    Responsables responsableSave = this.responsableDao.save(responsable);
                    membre.setResponsable(responsableSave);
                    membre.setOrganisation(responsableSave.getOrganisation());
                }
            }
        } else {
            final Accounts account = this.accountDao.selectById(currentUser.getId());
            List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
            membre.setOrganisation(organisations.get(0));
        }
        membre.setHasResponsable(request.isResponsable());
        membre.setLibelle(request.getLibelle());
        membre.setAdresse(request.getAdresse());
        if (!request.getTelephone().isBlank())
            membre.setTelephone(request.getTelephone());
        if (!request.getEmail().isBlank())
            membre.setEmail(request.getEmail());
        membre.setAccount(this.accountDao.selectById(currentUser.getId()));
        membre.setDateFin(request.getDateFin());
        membre.setActif(true);
        membre.setSlug(slug);
        Membres membreSave = this.membreDao.save(membre);
        if (membreSave!=null){
            if (request.getEquipes().size()>0){
                for (Equipes eq :
                        request.getEquipes()) {
                    Equipes equipe = this.equipeDao.selectById(eq.getId());
                    if (Helpers.compareDate(membreSave.getDateFin(), new Date())){
                        if (equipe.getOrganisation().getMembreEquActifs() == this.equipeMembreDao.countByEquipe(equipe, new Date())){
                            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'membres actifs atteint pour l'équipe", "Access denied"));
                        }
                    }


                    EquipeMembres equipeMembre = new EquipeMembres();
                    EquipeMembrePK pk = new EquipeMembrePK(equipe, membreSave);
                    equipeMembre.setId(pk);
                    this.equipeMembreDao.save(equipeMembre);
                }
            }
        }

        return ResponseEntity.ok(membreSave);
    }
    @RequestMapping(value = { "/membre/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> membreUpdate(@Valid @RequestBody final MembreRequest request,
                                         @PathVariable(value = "id") Long id,
                                          @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {
        if (!request.getEmail().isBlank()){
            if (membreDao.existsByEmail(request.getEmail(), id)){
                return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error email already exists", "Error duplication key"));
            }
        }

        Membres membre = this.membreDao.selectById(id);
        if (!request.isResponsable()){
            if (request.getEmailResponsable() != null){
                if (this.responsableDao.existsByEmail(request.getEmailResponsable())){
                    Responsables responsableSave = this.responsableDao.selectByEmail(request.getEmailResponsable());
                    membre.setResponsable(responsableSave);
                } else {
                    final Accounts account = this.accountDao.selectById(currentUser.getId());
                    List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
                    Responsables responsable = new Responsables();
                    responsable.setLibelle(request.getLibelleResponsable());
                    responsable.setEmail(request.getEmailResponsable());
                    responsable.setTelephone(request.getTelephoneResponsable());
                    responsable.setAdresse(request.getAdresseResponsable());
                    if (organisations.size()>0)
                        responsable.setOrganisation(organisations.get(0));

                    Responsables responsableSave = this.responsableDao.save(responsable);
                    membre.setResponsable(responsableSave);
                }
            }
        }else {
            final Accounts account = this.accountDao.selectById(currentUser.getId());
            List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
            membre.setOrganisation(organisations.get(0));
        }
        membre.setHasResponsable(request.isResponsable());
        membre.setLibelle(request.getLibelle());
        membre.setAdresse(request.getAdresse());
        if (!request.getTelephone().isBlank())
            membre.setTelephone(request.getTelephone());
        if (!request.getEmail().isBlank())
            membre.setEmail(request.getEmail());
        membre.setDateFin(request.getDateFin());
        membre.setActif(true);
        Membres membreSave = this.membreDao.save(membre);

        if (membreSave!=null){
            if (request.getEquipes().size()>0){

                List<EquipeMembres> equipeMembres = this.equipeMembreDao.selectByEquipe(membreSave);
                this.equipeMembreDao.delete(equipeMembres);

                for (Equipes eq :
                        request.getEquipes()) {
                    Equipes equipe = this.equipeDao.selectById(eq.getId());
                    if (Helpers.compareDate(membreSave.getDateFin(), new Date())){
                        if (equipe.getOrganisation().getMembreEquActifs() == this.equipeMembreDao.countByEquipe(equipe, new Date())){
                            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'membres actifs atteint pour l'équipe", "Access denied"));
                        }
                    }

                    EquipeMembres equipeMembre = new EquipeMembres();
                    EquipeMembrePK pk = new EquipeMembrePK(equipe, membreSave);
                    equipeMembre.setId(pk);
                    this.equipeMembreDao.save(equipeMembre);
                }
            }
        }
        return ResponseEntity.ok(membreSave);
    }

    @RequestMapping(value = { "/membre/add/equipe/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> membreAddEquipe(@Valid @RequestBody final MembreRequest request,
                                         @PathVariable(value = "id") Long id,
                                          @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {
        Membres membre = this.membreDao.selectById(id);

        if (membre!=null){
            if (request.getEquipes().size()>0){
                for (Equipes eq :
                        request.getEquipes()) {
                    Equipes equipe = this.equipeDao.selectById(eq.getId());
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
        return ResponseEntity.ok(new ApiMessage(HttpStatus.OK, "equipe ajouter avec succes"));
    }
    @RequestMapping(value = { "/membre/check/email/exist" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkEmail(@RequestParam(name="email") String email) {
        return this.membreDao.existsByEmail(email);
    }
    @RequestMapping(value = { "/membre/check/email/exist/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkEmail(@RequestParam(name="email") String email,
                              @PathVariable(value = "id") Long id) {
        return this.membreDao.existsByEmail(email, id);
    }
    @RequestMapping(value = { "/membre/delete/{id}" }, method = { RequestMethod.DELETE })
    public void membreUpdate(@PathVariable(value = "id") Long id) throws MessagingException {
        final Membres membre = this.membreDao.selectById(id);
        this.membreDao.delete(membre);
    }

    @RequestMapping(value = { "/membres/delete" }, method = { RequestMethod.DELETE })
    public void equipeDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long id :
                    request.getIds()) {
                Membres membre = this.membreDao.selectById(id);
                this.membreDao.delete(membre);
            }
        }
    }

    @RequestMapping(value = { "/membres/equipes/delete/{id}" }, method = { RequestMethod.DELETE })
    public void equipeDelete(@PathVariable(value = "id") Long id,
                             @Valid @RequestBody final LongRequest request) {
        Membres membre = this.membreDao.selectById(id);

        if (membre!=null){
            if (request.getIds().size()>0){
                for (Long eq :
                        request.getIds()) {
                    Equipes equipe = this.equipeDao.selectById(eq);
                    EquipeMembrePK pk = new EquipeMembrePK(equipe, membre);
                    EquipeMembres equipeMembre = this.equipeMembreDao.selectById(pk);
//                    equipeMembre.setId(pk);
                    this.equipeMembreDao.delete(equipeMembre);
                }
            }
        }
    }

    @RequestMapping(value = { "/membres" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAll() {
        List<Membres> membres = this.membreDao.findByDeletedFalseOrderByIdDesc();

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        return membreDtos;
    }
    @RequestMapping(value = { "/membres/evenement/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectEvenement(@PathVariable(value = "id") Long id) {
        Evenements evenement = this.evenementDao.selectById(id);
        List<Long> ids = this.evenementEquipeDao.selectByEvenementIds(evenement);
        List<Membres> membres = this.equipeMembreDao.selectByEquipes(ids);

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        return membreDtos;
    }
    @RequestMapping(value = { "/membres/equipe/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAll(@PathVariable(value = "id") Long id) {
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Membres> membres = this.equipeMembreDao.selectByEquipe(equipe);

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        return membreDtos;
    }
    @RequestMapping(value = { "/membres/equipe/actif/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAllActif(@PathVariable(value = "id") Long id) {
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Membres> membres = this.equipeMembreDao.membreByEquipe(equipe, new Date());

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        return membreDtos;
    }
    @RequestMapping(value = { "/membres/equipes/actif" }, method = { RequestMethod.POST })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAllActif(@Valid @RequestBody final LongRequest request) {
        List<MembreDto> membreDtos = new ArrayList<>();
        for (Long id :
                request.getIds()) {
            final Equipes equipe = this.equipeDao.selectById(id);
            List<Membres> membres = this.equipeMembreDao.membreByEquipe(equipe, new Date());
            membres.forEach(membre ->{
                List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
                MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
                membreDto.setEquipes(equipes);
                membreDtos.add(membreDto);
            });
        }

        return membreDtos;
    }
    @RequestMapping(value = { "/membres/equipe/actif/not/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAllActifNot(@PathVariable(value = "id") Long id,
                                             @CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Membres> membres = this.membreDao.selectByOrganisation(ids, equipe, new Date());

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        return membreDtos;
    }
    @RequestMapping(value = { "/membres/organisation" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAll(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Membres> membres = this.membreDao.selectByOrganisation(ids);

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        return membreDtos;
    }
    @RequestMapping(value = { "/membres/organisation/actif" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAllActif(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Membres> membres = this.membreDao.selectByOrganisation(ids, new Date());
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        return membreDtos;
    }
    @RequestMapping(value ="/membres/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Membres> membres = this.membreDao.findByDeletedFalseOrderByIdDesc(pageable);

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });

        ResponsePage pages = new ResponsePage();

        Long total = this.membreDao.countMembres();
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
            pages.setFirst_page_url(url_membre_page+1);
            pages.setLast_page_url(url_membre_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_membre_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_membre_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(membreDtos);
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
    @RequestMapping(value = "/membres/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Membres> membres = this.membreDao.recherche(s, pageable);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.membreDao.countRecherche(s);
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
            pages.setFirst_page_url(url_membre_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_membre_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_membre_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_membre_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(membreDtos);

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
    @RequestMapping(value ="/membres/organisation/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @CurrentUser final UserDetailsImpl currentUser){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Membres> membres = this.membreDao.selectByOrganisation(ids, pageable);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        ResponsePage pages = new ResponsePage();

        Long total = this.membreDao.countByOrganisation(ids);
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
            pages.setFirst_page_url(url_membre_org_page+1);
            pages.setLast_page_url(url_membre_org_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_membre_org_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_membre_org_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(membreDtos);
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
    @RequestMapping(value = "/membres/organisation/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(name="s") String s,
                                   @CurrentUser final UserDetailsImpl currentUser){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Membres> membres = this.membreDao.recherche(ids, s, pageable);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.membreDao.countRecherche(ids, s);
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
            pages.setFirst_page_url(url_membre_org_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_membre_org_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_membre_org_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_membre_org_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(membreDtos);

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
    @RequestMapping(value ="/membres/equipe/page/{id}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @PathVariable(value = "id") Long id){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Membres> membres = this.equipeMembreDao.selectByEquipe(equipe, pageable);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        ResponsePage pages = new ResponsePage();

        Long total = this.equipeMembreDao.countByEquipe(equipe);
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
            pages.setFirst_page_url(url_membre_eq_page+id+"/"+1);
            pages.setLast_page_url(url_membre_eq_page+id+"/"+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_membre_eq_page+id+"/"+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_membre_eq_page+id+"/"+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(membreDtos);
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
    @RequestMapping(value = "/membres/equipe/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(
                                    @RequestParam(name="page") int page,
                                    @RequestParam(value = "id") Long id,
                                   @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Membres> membres = this.equipeMembreDao.recherche(equipe, s, pageable);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.equipeMembreDao.countRecherche(equipe, s);
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
            pages.setFirst_page_url(url_membre_eq_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_membre_eq_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_membre_eq_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_membre_eq_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(membreDtos);

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

    @RequestMapping(value ="/membres/filter/page/{page}", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePage selectPageFilter(@PathVariable(value = "page") int page,
                                         @Valid @RequestBody final FilterRequest request,
                                         @CurrentUser final UserDetailsImpl currentUser) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Membres> membres = new ArrayList<>();
        Long total = 0L;
        if (request.getEtats().size() == 2){
            if (request.getNoms().size() > 0){
                membres = this.membreDao.recherche(ids, request.getNoms(), pageable);
                total = this.membreDao.countRecherche(ids, request.getNoms());
            } else {
                membres = this.membreDao.recherche(ids, pageable);
                total = this.membreDao.countRecherche(ids);
            }
        } else if (request.getEtats().size() == 0) {
            membres = this.membreDao.recherche(ids, request.getNoms(), pageable);
            total = this.membreDao.countRecherche(ids, request.getNoms());
        } else {
            if (request.getEtats().size() == 1){
                Boolean etat = request.getEtats().get(0);
                if (etat==true){
                    membres = this.membreDao.rechercheActif(ids, request.getNoms(), new Date(), pageable);
                    total = this.membreDao.countRechercheActif(ids, request.getNoms(), new Date());
                }else {
                    membres = this.membreDao.rechercheInactif(ids, request.getNoms(), new Date(), pageable);
                    total = this.membreDao.countRechercheInactif(ids, request.getNoms(), new Date());
                }
            }
        }

        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });

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
            pages.setFirst_page_url(url_membre_eq_page+1);
            pages.setLast_page_url(url_membre_eq_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_membre_eq_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_membre_eq_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(membreDtos);
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
