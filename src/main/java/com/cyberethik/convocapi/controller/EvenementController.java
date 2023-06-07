package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.persistance.dto.EvenementDto;
import com.cyberethik.convocapi.persistance.dto.MembreDto;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.persistance.utils.UtilEvenement;
import com.cyberethik.convocapi.persistance.utils.UtilMembre;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.EvenementRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
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
    public EvenementController() {
    }
    
    @RequestMapping(value = { "/evenement/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> evenementSave(@Valid @RequestBody final EvenementRequest request,
                                        @CurrentUser final UserDetailsImpl currentUser) throws MessagingException {


        Evenements evenement = new Evenements();
        evenement.setLibelle(request.getLibelle());
        evenement.setDescription(request.getDescription());
        evenement.setDateDebut(request.getDateDebut());
        evenement.setDateFin(request.getDateFin());

        Evenements evenementSave = this.evenementDao.save(evenement);
        if (evenementSave!=null){
            if (request.getEquipes().size()>0){
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
        Evenements evenement = this.evenementDao.selectById(id);
        evenement.setLibelle(request.getLibelle());
        evenement.setDescription(request.getDescription());
        evenement.setDateDebut(request.getDateDebut());
        evenement.setDateFin(request.getDateFin());

        Evenements evenementSave = this.evenementDao.save(evenement);
        if (evenementSave!=null){
            if (request.getEquipes().size()>0){

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
    @RequestMapping(value = { "/evenements/organisation" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<EvenementDto> selectAll(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Evenements> evenements = this.evenementEquipeDao.selectByOrganisation(ids);
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
        List<Evenements> evenements = this.evenementEquipeDao.selectByOrganisation(ids, pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });
        ResponsePage pages = new ResponsePage();

        Long total = this.evenementEquipeDao.countByOrganisation(ids);
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
        List<Evenements> evenements = this.evenementEquipeDao.recherche(ids, s, pageable);
        List<EvenementDto> evenementDtos = new ArrayList<>();

        evenements.forEach(evenement ->{
            List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);
            EvenementDto evenementDto = UtilEvenement.convertToDto(evenement, modelMapper);
            evenementDto.setEquipes(equipes);
            evenementDtos.add(evenementDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.evenementEquipeDao.countRecherche(ids, s);
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

}
