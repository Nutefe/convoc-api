package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.persistance.dto.EvenementDto;
import com.cyberethik.convocapi.persistance.dto.MembreDto;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.persistance.utils.UtilEvenement;
import com.cyberethik.convocapi.persistance.utils.UtilMembre;
import com.cyberethik.convocapi.playload.helper.Helpers;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.EvenementRequest;
import com.cyberethik.convocapi.playload.request.FilterRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.playload.response.EvenementResponse;
import com.cyberethik.convocapi.playload.response.MembreResponse;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cyberethik.convocapi.playload.helper.Helpers.sortByCreatedDesc;

@RestController
@RequestMapping({ "/web/service" })
public class EvenementController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_evenement_page}")
    private String url_evenement_page;
    @Value("${app.url_evenement_search_page}")
    private String url_evenement_search_page;
    @Value("${app.url_evenement_org_page}")
    private String url_evenement_org_page;
    @Value("${app.url_evenement_org_search_page}")
    private String url_evenement_org_search_page;
    @Value("${app.url_evenement_eq_page}")
    private String url_evenement_eq_page;
    @Value("${app.url_evenement_eq_search_page}")
    private String url_evenement_eq_search_page;
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
    @Autowired
    private ConvocationDao convocationDao;
    @Autowired
    private ReponseDao reponseDao;
    public EvenementController() {
    }
    
    @RequestMapping(value = { "/evenement/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> evenementSave(@Valid @RequestBody final EvenementRequest request,
                                        @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {

        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
        Organisations organisationInit = organisations.get(0);

        if (organisationInit != null){
            if (organisationInit.getEvenementActifs() == this.evenementDao.countEvenements(organisationInit.getId(), new Date())){
                return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'évenements actifs atteint", "Access denied"));
            }
        }

        if (Helpers.compareDate(request.getDateDebut(), request.getDateFin())){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, "La date de debut ne peut pas etre apres la date de fin", "BAD_REQUEST"));
        }

        Evenements evenement = new Evenements();
        evenement.setLibelle(request.getLibelle());
        evenement.setDescription(request.getDescription());
        evenement.setDateDebut(request.getDateDebut());
        evenement.setDateFin(request.getDateFin());
        evenement.setOrganisation(organisationInit);
        evenement.setCoordinateur(this.membreDao.selectById(request.getCoordinateur().getId()));

        Evenements evenementSave = this.evenementDao.save(evenement);
        if (evenementSave!=null){
            if (request.getEquipes().size()>0){
                if (request.getEquipes().size() > 2){
                    return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre de membre est atteint", "Access denied"));
                }
                for (Equipes eq :
                        request.getEquipes()) {
                    Equipes equipe = this.equipeDao.selectById(eq.getId());
                    EvenementEquipes evenementEquipe = new EvenementEquipes();
                    EvenementEquipePK pk = new EvenementEquipePK(evenementSave, equipe);
                    evenementEquipe.setId(pk);
                    this.evenementEquipeDao.save(evenementEquipe);
                }
            }
        }

        return ResponseEntity.ok(evenementSave);
    }
    @RequestMapping(value = { "/evenement/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> evenementUpdate(@Valid @RequestBody final EvenementRequest request,
                                         @PathVariable(value = "id") Long id) throws MessagingException {
        if (Helpers.compareDate(request.getDateDebut(), request.getDateFin())){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, "La date de debut ne peut pas etre apres la date de fin", "BAD_REQUEST"));
        }

        Evenements evenement = this.evenementDao.selectById(id);
        evenement.setLibelle(request.getLibelle());
        evenement.setDescription(request.getDescription());
        evenement.setDateDebut(request.getDateDebut());
        evenement.setDateFin(request.getDateFin());

        Evenements evenementSave = this.evenementDao.save(evenement);
        if (evenementSave!=null){
            if (request.getEquipes().size()>0){
                if (request.getEquipes().size() > 2){
                    return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre de membre est atteint", "Access denied"));
                }
                List<EvenementEquipes> evenementEquipes = this.evenementEquipeDao.selectByEquipe(evenementSave);
                this.evenementEquipeDao.delete(evenementEquipes);

                for (Equipes eq :
                        request.getEquipes()) {
                    Equipes equipe = this.equipeDao.selectById(eq.getId());
                    EvenementEquipePK pk = new EvenementEquipePK(evenementSave, equipe);
                    EvenementEquipes evenementEquipe = new EvenementEquipes();
                    evenementEquipe.setId(pk);
                    this.evenementEquipeDao.save(evenementEquipe);
                }
            }
        }

        return ResponseEntity.ok(evenementSave);
    }
    
    @RequestMapping(value = { "/evenement/suivi/{id}" }, method = { RequestMethod.GET })
    public ResponseEntity<?> suiviEvenement(@PathVariable(value = "id") Long id) throws MessagingException {
        final Evenements evenement = this.evenementDao.selectById(id);
        Convocations convocation = this.convocationDao.findTop1ByEvenementAndDeletedFalse(evenement);
        Long nbrPersConvoc = this.convocationDao.countConvocations(evenement);
        Long nbrReponseRecu = this.reponseDao.countReponses(evenement);
        Long nbrReponsePositif = this.reponseDao.countReponsePositif(evenement);
        Long nbrReponseNegatif = this.reponseDao.countReponseNegatif(evenement);
        Long nbrReponseNeant = nbrPersConvoc - nbrReponseRecu;
        EvenementResponse response = new EvenementResponse();
        response.setEvenement(evenement);
        if (convocation!=null)
            response.setDateEnvoie(convocation.getDateEnvoi());
        response.setNbrPersConvoc(nbrPersConvoc);
        response.setNbrReponseRecu(nbrReponseRecu);
        response.setNbrReponsePositif(nbrReponsePositif);
        response.setNbrReponseNegatif(nbrReponseNegatif);
        response.setNbrReponseNeant(nbrReponseNeant);
        if (nbrPersConvoc > 0){
            DecimalFormat decfor = new DecimalFormat("0.00");
            response.setPourcReponseRecu(decfor.format(((double)nbrReponseRecu/nbrPersConvoc) *100));
            response.setPourcReponsePositif(decfor.format(((double)nbrReponsePositif/nbrPersConvoc)*100));
            response.setPourcReponseNegatif(decfor.format(((double)nbrReponseNegatif/nbrPersConvoc)*100));
            response.setPourcReponseNeant(decfor.format(((double)nbrReponseNeant/nbrPersConvoc)*100));
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = { "/evenement/delete/{id}" }, method = { RequestMethod.DELETE })
    public void evenementUpdate(@PathVariable(value = "id") Long id) throws MessagingException {
        final Evenements evenement = this.evenementDao.selectById(id);
        this.evenementDao.delete(evenement);
    }

    @RequestMapping(value = { "/evenements/delete" }, method = { RequestMethod.DELETE })
    public void evenementDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long id :
                    request.getIds()) {
                Evenements evenement = this.evenementDao.selectById(id);
                this.evenementDao.delete(evenement);
            }
        }
    }
    @RequestMapping(value = { "/evenements" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<EvenementDto> selectAll() {
        List<Evenements> evenements = this.evenementDao.findByDeletedFalseOrderByIdDesc();
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });

        return evenementDtos;
    }
    @RequestMapping(value = { "/evenements/equipe/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<EvenementDto> selectAll(@PathVariable(value = "id") Long id) {
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Evenements> evenements = this.evenementEquipeDao.selectByEquipe(equipe);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });

        return evenementDtos;
    }
    @RequestMapping(value = { "/evenements/membre/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<EvenementDto> selectAllMembre(@PathVariable(value = "id") Long id) {
        final Membres membre = this.membreDao.selectById(id);
        List<Long> ids = this.equipeMembreDao.selectByMembreIds(membre);
        List<Evenements> evenements = this.evenementEquipeDao.selectByEquipe(ids);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });

        return evenementDtos;
    }
    @RequestMapping(value = { "/membres/invite/evenement/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAllMembres(@PathVariable(value = "id") Long id) {
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Membres> membres = this.convocationDao.selectMembreByEvenement(evenement);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });

        return membreDtos;
    }
    @RequestMapping(value = { "/evenements/organisation" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<EvenementDto> selectAll(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Evenements> evenements = this.evenementDao.selectByOrganisation(ids);

        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });

        return evenementDtos;
    }
    @RequestMapping(value ="/evenements/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Evenements> evenements = this.evenementDao.findByDeletedFalseOrderByIdDesc(pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });

        ResponsePage pages = new ResponsePage();

        Long total = this.evenementDao.countEvenements();
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
            pages.setFirst_page_url(url_evenement_page+1);
            pages.setLast_page_url(url_evenement_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(evenementDtos);
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
    @RequestMapping(value = "/evenements/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Evenements> evenements = this.evenementDao.recherche(s, pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.evenementDao.countRecherche(s);
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
            pages.setFirst_page_url(url_evenement_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_evenement_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(evenementDtos);

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
    @RequestMapping(value ="/evenements/organisation/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @CurrentUser final UserDetailsImpl currentUser){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Evenements> evenements = this.evenementDao.selectByOrganisation(ids, pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });
        ResponsePage pages = new ResponsePage();

        Long total = this.evenementDao.countByOrganisation(ids);
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
            pages.setFirst_page_url(url_evenement_org_page+1);
            pages.setLast_page_url(url_evenement_org_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_org_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_org_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(evenementDtos);
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
    @RequestMapping(value = "/evenements/organisation/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(name="s") String s,
                                   @CurrentUser final UserDetailsImpl currentUser){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Evenements> evenements = this.evenementDao.recherche(ids, s, pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.evenementDao.countRecherche(ids, s);
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
            pages.setFirst_page_url(url_evenement_org_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_evenement_org_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_org_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_org_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(evenementDtos);

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
    @RequestMapping(value ="/evenements/equipe/page/{id}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @PathVariable(value = "id") Long id){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Evenements> evenements = this.evenementEquipeDao.selectByEquipe(equipe, pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });
        ResponsePage pages = new ResponsePage();

        Long total = this.evenementEquipeDao.countByEquipe(equipe);
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
            pages.setFirst_page_url(url_evenement_eq_page+id+"/"+1);
            pages.setLast_page_url(url_evenement_eq_page+id+"/"+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_eq_page+id+"/"+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_eq_page+id+"/"+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(evenementDtos);
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
    @RequestMapping(value = "/evenements/equipe/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(value = "id") Long id,
                                   @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Equipes equipe = this.equipeDao.selectById(id);
        List<Evenements> evenements = this.evenementEquipeDao.recherche(equipe, s, pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.evenementEquipeDao.countRecherche(equipe, s);
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
            pages.setFirst_page_url(url_evenement_eq_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_evenement_eq_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_eq_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_eq_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(evenementDtos);

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
    @RequestMapping(value = { "/status/evenement/encours" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> countMonth(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
        Organisations organisationInit = organisations.get(0);
        return ResponseEntity.ok(this.evenementDao.countEvenements(organisationInit.getId(), new Date()));
    }

    @RequestMapping(value ="/membres/convocations/evenement/page/{id}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPageMembre(@PathVariable(value = "page") int page,
                                   @PathVariable(value = "id") Long id){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
        List<Membres> membres = this.convocationDao.selectMembreByEvenement(evenement, pageable);

        List<MembreResponse> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Reponses> reponses = this.reponseDao.membreEvenement(evenement, membre);
            MembreResponse membreDto = new MembreResponse();
            membreDto.setEquipes(equipes);
            membreDto.setMembre(membre);
            if (reponses.size()>0)
                membreDto.setResponse(reponses.get(0));
            else
                membreDto.setResponse(null);
            membreDtos.add(membreDto);
        });

        ResponsePage pages = new ResponsePage();

        Long total = this.convocationDao.countConvocationsMembre(evenement);
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
            pages.setFirst_page_url(url_evenement_eq_page+id+"/"+1);
            pages.setLast_page_url(url_evenement_eq_page+id+"/"+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_eq_page+id+"/"+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_eq_page+id+"/"+(page-1));
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
    @RequestMapping(value = "/membres/convocations/evenement/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPageMembre(@RequestParam(name="page") int page,
                                   @RequestParam(value = "id") Long id,
                                   @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
        List<Membres> membres = this.convocationDao.rechercheMembre(evenement, s, pageable);
        List<MembreResponse> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Reponses> reponses = this.reponseDao.membreEvenement(evenement, membre);
            MembreResponse membreDto = new MembreResponse();
            membreDto.setEquipes(equipes);
            membreDto.setMembre(membre);
            if (reponses.size()>0)
                membreDto.setResponse(reponses.get(0));
            else
                membreDto.setResponse(null);
            membreDtos.add(membreDto);
        });

        ResponsePage pages = new ResponsePage();
        Long total = this.convocationDao.countRechercheMembre(evenement, s);
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
            pages.setFirst_page_url(url_evenement_eq_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_evenement_eq_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_eq_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_eq_page+"?page="+(page-1)+"&s="+s);
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
    @RequestMapping(value ="/membres/convocations/evenement/filter/page/{id}/{page}",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponsePage selectPageFilter(@PathVariable(value = "id") Long id,
                                         @PathVariable(value = "page") int page,
                                         @Valid @RequestBody final FilterRequest request,
                                         @CurrentUser final UserDetailsImpl currentUser) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
//        final Accounts account = this.accountDao.selectById(currentUser.getId());
//        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
        List<Membres> membres;
        Long total;
        if (request.getDateReponse().size() >= 2  || request.getReponses().size() > 0){
            if (request.getDateReponse().size() >= 2){
                membres = this.reponseDao.recherche(evenement, request.getEquipes(), request.getMembres(), request.getDateReponse().get(0), request.getDateReponse().get(1), request.getReponses(), pageable);
                total = this.reponseDao.countRecherche(evenement, request.getEquipes(), request.getMembres(),  request.getDateReponse().get(0), request.getDateReponse().get(1), request.getReponses());
            } else {
                membres = this.reponseDao.recherche(evenement, request.getEquipes(), request.getMembres(), request.getReponses(), pageable);
                total = this.reponseDao.countRecherche(evenement, request.getEquipes(), request.getMembres(), request.getReponses());
            }
        } else {
            membres = this.convocationDao.rechercheMembre(evenement, request.getEquipes(), request.getMembres(), pageable);
            total = this.convocationDao.countRechercheMembre(evenement, request.getEquipes(), request.getMembres());
        }

        List<MembreResponse> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Reponses> reponses = this.reponseDao.membreEvenement(evenement, membre);
            MembreResponse membreDto = new MembreResponse();
            membreDto.setEquipes(equipes);
            membreDto.setMembre(membre);
            if (reponses.size()>0)
                membreDto.setResponse(reponses.get(0));
            else
                membreDto.setResponse(null);
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
            pages.setFirst_page_url(url_evenement_eq_page+1);
            pages.setLast_page_url(url_evenement_eq_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_eq_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_eq_page+(page-1));
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
    @RequestMapping(value ="/evenements/filter/page/{page}", method = RequestMethod.POST)
    @ResponseBody
    public ResponsePage selectPageFilter(@PathVariable(value = "page") int page,
                                         @Valid @RequestBody final FilterRequest request,
                                         @CurrentUser final UserDetailsImpl currentUser) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Evenements> evenements = new ArrayList<>();
        Long total = 0L;
        if (request.getEtats().size() == 2){
            if (request.getNoms().size() > 0){
                evenements = this.evenementDao.recherche(ids, request.getNoms(), pageable);
                total = this.evenementDao.countRecherche(ids, request.getNoms());
            } else {
                evenements = this.evenementDao.recherche(ids, pageable);
                total = this.evenementDao.countRecherche(ids);
            }
        } else if (request.getEtats().size() == 0) {
            evenements = this.evenementDao.recherche(ids, request.getNoms(), pageable);
            total = this.evenementDao.countRecherche(ids, request.getNoms());
        } else {
            if (request.getEtats().size() == 1){
                Boolean etat = request.getEtats().get(0);
                if (etat==true){
                    evenements = this.evenementDao.rechercheActif(ids, request.getNoms(), new Date(), pageable);
                    total = this.evenementDao.countRechercheActif(ids, request.getNoms(), new Date());
                }else {
                    evenements = this.evenementDao.rechercheInactif(ids, request.getNoms(), new Date(), pageable);
                    total = this.evenementDao.countRechercheInactif(ids, request.getNoms(), new Date());
                }
            }
        }

        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
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
            pages.setFirst_page_url(url_evenement_eq_page+1);
            pages.setLast_page_url(url_evenement_eq_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_evenement_eq_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_evenement_eq_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(evenementDtos);
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
