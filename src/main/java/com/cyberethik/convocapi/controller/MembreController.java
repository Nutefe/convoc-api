package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.persistance.dto.AccountDto;
import com.cyberethik.convocapi.persistance.dto.EquipeDto;
import com.cyberethik.convocapi.persistance.dto.MembreDto;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.persistance.utils.UtilAccount;
import com.cyberethik.convocapi.persistance.utils.UtilEquipe;
import com.cyberethik.convocapi.persistance.utils.UtilMembre;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.playload.request.MembreRequest;
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

        String slug;
        do {
            int length = 10;
            boolean useLetters = true;
            boolean useNumbers = true;
            slug = RandomStringUtils.random(length, useLetters, useNumbers);
        }while (this.membreDao.existsBySlug(slug));

        Membres membre = new Membres();
        membre.setLibelle(request.getLibelle());
        membre.setAdresse(request.getAdresse());
        membre.setResponsable(this.responsableDao.selectById(request.getResponsable().getId()));
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
                                         @PathVariable(value = "id") Long id) throws MessagingException {
        Membres membre = this.membreDao.selectById(id);
        membre.setLibelle(request.getLibelle());
        membre.setAdresse(request.getAdresse());
        membre.setResponsable(this.responsableDao.selectById(request.getResponsable().getId()));
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
                    EquipeMembres equipeMembre = new EquipeMembres();
                    EquipeMembrePK pk = new EquipeMembrePK(equipe, membreSave);
                    equipeMembre.setId(pk);
                    this.equipeMembreDao.save(equipeMembre);
                }
            }
        }
        return ResponseEntity.ok(membreSave);
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
    @RequestMapping(value = { "/membres/organisation" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<MembreDto> selectAll(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);

        List<Membres> membres = this.equipeMembreDao.selectByOrganisation(ids);

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
        List<Membres> membres = this.equipeMembreDao.selectByOrganisation(ids, pageable);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        ResponsePage pages = new ResponsePage();

        Long total = this.equipeMembreDao.countByOrganisation(ids);
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
        List<Membres> membres = this.equipeMembreDao.recherche(ids, s, pageable);
        List<MembreDto> membreDtos = new ArrayList<>();

        membres.forEach(membre ->{
            List<Equipes> equipes = this.equipeMembreDao.selectByMembre(membre);
            MembreDto membreDto = UtilMembre.convertToDto(membre, modelMapper);
            membreDto.setEquipes(equipes);
            membreDtos.add(membreDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.equipeMembreDao.countRecherche(ids, s);
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

}
