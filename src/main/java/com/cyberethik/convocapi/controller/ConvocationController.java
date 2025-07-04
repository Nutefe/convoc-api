package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.messaging.emails.model.Email;
import com.cyberethik.convocapi.messaging.emails.service.EmailSenderService;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.playload.helper.Helpers;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.ConvocationRequest;
import com.cyberethik.convocapi.playload.request.EvenementRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.cyberethik.convocapi.playload.helper.Helpers.sortByCreatedDesc;

@RestController
@RequestMapping({ "/web/service" })
public class ConvocationController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_convocation_page}")
    private String url_convocation_page;
    @Value("${app.url_convocation_search_page}")
    private String url_convocation_search_page;
    @Value("${app.url_convocation_org_page}")
    private String url_convocation_org_page;
    @Value("${app.url_convocation_org_search_page}")
    private String url_convocation_org_search_page;
    @Value("${app.url_convocation_eq_page}")
    private String url_convocation_eq_page;
    @Value("${app.url_convocation_eq_search_page}")
    private String url_convocation_eq_search_page;
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
    private final EmailSenderService emailSenderService;
    public ConvocationController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }
    
    @RequestMapping(value = { "/convocation/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> convocationSave(@Valid @RequestBody final ConvocationRequest request) throws MessagingException {
        Evenements evenement = this.evenementDao.selectById(request.getEvenement().getId());
        if (evenement.isEnvoyer()){
            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Cette convocation a déjà été envoyé", "Access denied"));
        }
        List<Evenements> evenements = this.convocationDao.selectConvocations(evenement.getOrganisation().getId(), Helpers.startOfWeek(), Helpers.endOfWeek());
        if (evenements.size() > 0){
            for (Evenements event :
                    evenements) {
                List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(event);
                for (Equipes equ :
                        equipes) {
                    List<Equipes> equipeSearch = this.evenementEquipeDao.selectByEvenement(evenement);
                    if (equipeSearch.contains(equ)){
                        return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Cette équipe a déjà envoyé une convocation cette semaine", "Access denied"));
                    }
                }
            }
        }
        if (evenement.getOrganisation().getConvocMax() ==
        this.convocationDao.countConvocations(evenement.getOrganisation().getId(), Helpers.startOfWeek(), Helpers.endOfWeek())){
            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre convocation atteint pour l'organisation", "Access denied"));
        }

        if (request.getMembres().size() > 0){
            for (Membres mem :
                    request.getMembres()) {

                String slug;
                do {
                    int length = 10;
                    boolean useLetters = true;
                    boolean useNumbers = true;
                    slug = RandomStringUtils.random(length, useLetters, useNumbers);
                }while (this.convocationDao.existsBySlug(slug));

                Membres membre = this.membreDao.selectById(mem.getId());
                Convocations convocation = new Convocations();
                convocation.setEvenement(evenement);
                convocation.setDateEnvoi(new Date());
                convocation.setMembre(membre);
                convocation.setSlug(slug);
                Convocations convocationSave=this.convocationDao.save(convocation);

                
                if (convocationSave!=null && membre != null){
                    if (membre.isHasResponsable()){
                        Email emailInit = new Email();
                        emailInit.setTo(membre.getEmail());
                        emailInit.setFrom(sender);
                        emailInit.setSubject("Convocation à un évènement");
                        emailInit.setTemplate("email-convocation.html");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("name", membre.getLibelle());
                        properties.put("membre", membre.getLibelle());
                        properties.put("dateDebut", Helpers.convertDate(evenement.getDateDebut()));
                        properties.put("heureDebut", Helpers.convertHeure1(evenement.getDateDebut()).replace(":", "h"));
                        properties.put("dateFin", Helpers.convertDate(evenement.getDateFin()));
                        properties.put("heureFin", Helpers.convertHeure1(evenement.getDateFin()).replace(":", "h"));
                        properties.put("evenement", evenement.getLibelle());
                        properties.put("link", Helpers.base_client_url+"pages/reponse/convoc/"+convocationSave.getSlug());
                        emailInit.setProperties(properties);
                        emailSenderService.sendHtmlMessage(emailInit);
                        convocationSave.setEnvoyer(true);
                        this.convocationDao.save(convocationSave);
                    }
                    else {
                        Email emailInit = new Email();
                        emailInit.setTo(membre.getResponsable().getEmail());
                        emailInit.setFrom(sender);
                        emailInit.setSubject("Convocation à un évènement");
                        emailInit.setTemplate("email-convocation.html");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("name", membre.getResponsable().getLibelle());
                        properties.put("membre", membre.getLibelle());
                        properties.put("dateDebut", Helpers.convertDate(evenement.getDateDebut()));
                        properties.put("heureDebut", Helpers.convertHeure1(evenement.getDateDebut()).replace(":", "h"));
                        properties.put("dateFin", Helpers.convertDate(evenement.getDateFin()));
                        properties.put("heureFin", Helpers.convertHeure1(evenement.getDateFin()).replace(":", "h"));
                        properties.put("evenement", evenement.getLibelle());
                        properties.put("link", Helpers.base_client_url+"pages/reponse/convoc/"+convocationSave.getSlug());
                        emailInit.setProperties(properties);
                        emailSenderService.sendHtmlMessage(emailInit);
                        convocationSave.setEnvoyer(true);
                        this.convocationDao.save(convocationSave);
                    }
                }
            }
            evenement.setEnvoyer(true);
            this.evenementDao.save(evenement);
        }

        return ResponseEntity.ok(new ApiMessage(HttpStatus.OK, "Convocation envoye avec succes"));
    }

    @RequestMapping(value = { "/convocation/equipe/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> convocationEquipeSave(@Valid @RequestBody final ConvocationRequest request) throws MessagingException {
        Evenements evenement = this.evenementDao.selectById(request.getEvenement().getId());
        if (evenement.isEnvoyer()){
            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Cette convocation a déjà été envoyé", "Access denied"));
        }
        List<Evenements> evenements = this.convocationDao.selectConvocations(evenement.getOrganisation().getId(), Helpers.startOfWeek(), Helpers.endOfWeek());
        if (evenements.size() > 0){
            for (Evenements event :
                    evenements) {
                List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(event);
                for (Equipes equ :
                        equipes) {
                    List<Equipes> equipeSearch = this.evenementEquipeDao.selectByEvenement(evenement);
                    if (equipeSearch.contains(equ)){
                        return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Cette équipe a déjà envoyé une convocation cette semaine", "Access denied"));
                    }
                }
            }
        }
        if (evenement.getOrganisation().getConvocMax() ==
                this.convocationDao.countConvocations(evenement.getOrganisation().getId(), Helpers.startOfWeek(), Helpers.endOfWeek())){
            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre de convocation atteint pour l'équipe", "Access denied"));
        }

        List<Membres> membres = new ArrayList<>();
        for (Equipes equipe :
                request.getEquipes()) {
            Equipes equipeIn = this.equipeDao.selectById(equipe.getId());
            List<Membres> memb = this.equipeMembreDao.selectByEquipe(equipeIn);
            membres.addAll(memb);
        }
        List<Membres> membresList = membres.stream().distinct().collect(Collectors.toList());
        if (membresList.size() > 0){
            for (Membres mem :
                    membresList) {

                String slug;
                do {
                    int length = 10;
                    boolean useLetters = true;
                    boolean useNumbers = true;
                    slug = RandomStringUtils.random(length, useLetters, useNumbers);
                }while (this.convocationDao.existsBySlug(slug));

                Membres membre = this.membreDao.selectById(mem.getId());
                Convocations convocation = new Convocations();
                convocation.setEvenement(evenement);
                convocation.setDateEnvoi(new Date());
                convocation.setMembre(membre);
                convocation.setSlug(slug);
                Convocations convocationSave=this.convocationDao.save(convocation);

                if (convocationSave!=null){
                    if (membre.isHasResponsable()){
                        Email emailInit = new Email();
                        emailInit.setTo(membre.getEmail());
                        emailInit.setFrom(sender);
                        emailInit.setSubject("Convocation à un évènement");
                        emailInit.setTemplate("email-convocation.html");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("name", membre.getLibelle());
                        properties.put("membre", membre.getLibelle());
                        properties.put("dateDebut", Helpers.convertDate(evenement.getDateDebut()));
                        properties.put("heureDebut", Helpers.convertHeure1(evenement.getDateDebut()).replace(":", "h"));
                        properties.put("dateFin", Helpers.convertDate(evenement.getDateFin()));
                        properties.put("heureFin", Helpers.convertHeure1(evenement.getDateFin()).replace(":", "h"));
                        properties.put("evenement", evenement.getLibelle());
                        properties.put("link", Helpers.base_client_url+"pages/reponse/convoc/"+convocationSave.getSlug());
                        emailInit.setProperties(properties);
                        emailSenderService.sendHtmlMessage(emailInit);
                        convocationSave.setEnvoyer(true);
                        this.convocationDao.save(convocationSave);
                    }
                    else {
                        Email emailInit = new Email();
                        emailInit.setTo(membre.getResponsable().getEmail());
                        emailInit.setFrom(sender);
                        emailInit.setSubject("Convocation à un évènement");
                        emailInit.setTemplate("email-convocation.html");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("name", membre.getResponsable().getLibelle());
                        properties.put("membre", membre.getLibelle());
                        properties.put("dateDebut", Helpers.convertDate(evenement.getDateDebut()));
                        properties.put("heureDebut", Helpers.convertHeure1(evenement.getDateDebut()).replace(":", "h"));
                        properties.put("dateFin", Helpers.convertDate(evenement.getDateFin()));
                        properties.put("heureFin", Helpers.convertHeure1(evenement.getDateFin()).replace(":", "h"));
                        properties.put("evenement", evenement.getLibelle());
                        properties.put("link", Helpers.base_client_url+"pages/reponse/convoc/"+convocationSave.getSlug());
                        emailInit.setProperties(properties);
                        emailSenderService.sendHtmlMessage(emailInit);
                        convocationSave.setEnvoyer(true);
                        this.convocationDao.save(convocationSave);
                    }
                }
            }
            evenement.setEnvoyer(true);
            this.evenementDao.save(evenement);
        }
        return ResponseEntity.ok(new ApiMessage(HttpStatus.OK, "Convocation envoye avec succes"));
    }
    @RequestMapping(value = { "/convocation/event/equipe/send/{id}" }, method = { RequestMethod.GET })
    public ResponseEntity<?> convocationEventEquipeSave(@PathVariable(value = "id") Long id) throws MessagingException {
        Evenements evenement = this.evenementDao.selectById(id);
        if (evenement.isEnvoyer()){
            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Cette convocation a déjà été envoyé", "Access denied"));
        }

        List<Evenements> evenements = this.convocationDao.selectConvocations(evenement.getOrganisation().getId(), Helpers.startOfWeek(), Helpers.endOfWeek());
        if (evenements.size() > 0){
            for (Evenements event :
                    evenements) {
                List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(event);
                for (Equipes equ :
                        equipes) {
                    List<Equipes> equipeSearch = this.evenementEquipeDao.selectByEvenement(evenement);
                    if (equipeSearch.contains(equ)){
                        return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Cette équipe a déjà envoyé une convocation cette semaine", "Access denied"));
                    }
                }
            }
        }

        if (evenement.getOrganisation().getConvocMax() ==
                this.convocationDao.countConvocations(evenement.getOrganisation().getId(), Helpers.startOfWeek(), Helpers.endOfWeek())){
            return  ResponseEntity.status(HttpStatus.LENGTH_REQUIRED).body(new ApiError(HttpStatus.LENGTH_REQUIRED, "Nombre d'membres actifs atteint pour l'équipe", "Access denied"));
        }

        List<Equipes> equipes = this.evenementEquipeDao.selectByEvenement(evenement);

        List<Membres> membres = new ArrayList<>();
        for (Equipes equipe :
                equipes) {
            Equipes equipeIn = this.equipeDao.selectById(equipe.getId());
            List<Membres> memb = this.equipeMembreDao.membreByEquipe(equipeIn, new Date());
            membres.addAll(memb);
        }
        List<Membres> membresList = membres.stream().distinct().collect(Collectors.toList());
        if (membresList.size() > 0){
            for (Membres mem :
                    membresList) {

                String slug;
                do {
                    int length = 10;
                    boolean useLetters = true;
                    boolean useNumbers = true;
                    slug = RandomStringUtils.random(length, useLetters, useNumbers);
                }while (this.convocationDao.existsBySlug(slug));

                Membres membre = this.membreDao.selectById(mem.getId());
                Convocations convocation = new Convocations();
                convocation.setEvenement(evenement);
                convocation.setDateEnvoi(new Date());
                convocation.setMembre(membre);
                convocation.setSlug(slug);
                Convocations convocationSave=this.convocationDao.save(convocation);

                if (convocationSave!=null){
                    if (membre.isHasResponsable()){
                        Email emailInit = new Email();
                        emailInit.setTo(membre.getEmail());
                        emailInit.setFrom(sender);
                        emailInit.setSubject("Convocation à un évènement");
                        emailInit.setTemplate("email-convocation.html");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("name", membre.getLibelle());
                        properties.put("membre", membre.getLibelle());
                        properties.put("dateDebut", Helpers.convertDate(evenement.getDateDebut()));
                        properties.put("heureDebut", Helpers.convertHeure1(evenement.getDateDebut()).replace(":", "h"));
                        properties.put("dateFin", Helpers.convertDate(evenement.getDateFin()));
                        properties.put("heureFin", Helpers.convertHeure1(evenement.getDateFin()).replace(":", "h"));
                        properties.put("evenement", evenement.getLibelle());
                        properties.put("link", Helpers.base_client_url+"pages/reponse/convoc/"+convocationSave.getSlug());
                        emailInit.setProperties(properties);
                        emailSenderService.sendHtmlMessage(emailInit);
                        convocationSave.setEnvoyer(true);
                        this.convocationDao.save(convocationSave);
                    }
                    else {
                        Email emailInit = new Email();
                        emailInit.setTo(membre.getResponsable().getEmail());
                        emailInit.setFrom(sender);
                        emailInit.setSubject("Convocation à un évènement");
                        emailInit.setTemplate("email-convocation.html");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("name", membre.getResponsable().getLibelle());
                        properties.put("membre", membre.getLibelle());
                        properties.put("dateDebut", Helpers.convertDate(evenement.getDateDebut()));
                        properties.put("heureDebut", Helpers.convertHeure1(evenement.getDateDebut()).replace(":", "h"));
                        properties.put("dateFin", Helpers.convertDate(evenement.getDateFin()));
                        properties.put("heureFin", Helpers.convertHeure1(evenement.getDateFin()).replace(":", "h"));
                        properties.put("evenement", evenement.getLibelle());
                        properties.put("link", Helpers.base_client_url+"pages/reponse/convoc/"+convocationSave.getSlug());
                        emailInit.setProperties(properties);
                        emailSenderService.sendHtmlMessage(emailInit);
                        convocationSave.setEnvoyer(true);
                        this.convocationDao.save(convocationSave);
                    }
                }
            }
            evenement.setEnvoyer(true);
            this.evenementDao.save(evenement);
        }
        return ResponseEntity.ok(new ApiMessage(HttpStatus.OK, "Convocation envoye avec succes"));
    }

    @RequestMapping(value = { "/convocation/slug/{slug}" }, method = { RequestMethod.GET })
    public ResponseEntity<?> convocationUpdate(@PathVariable(value = "slug") String slug) throws MessagingException {
        final Convocations convocation = this.convocationDao.selectBySlug(slug);
        return ResponseEntity.ok(convocation);
    }
    @RequestMapping(value = { "/convocation/delete/{id}" }, method = { RequestMethod.DELETE })
    public void convocationUpdate(@PathVariable(value = "id") Long id) throws MessagingException {
        final Convocations convocation = this.convocationDao.selectById(id);
        this.convocationDao.delete(convocation);
    }

    @RequestMapping(value = { "/convocations/delete" }, method = { RequestMethod.DELETE })
    public void convocationDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long id :
                    request.getIds()) {
                Convocations convocation = this.convocationDao.selectById(id);
                this.convocationDao.delete(convocation);
            }
        }
    }
    @RequestMapping(value = { "/convocations" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Convocations> selectAll() {
        return this.convocationDao.findByDeletedFalseOrderByIdDesc();
    }
    
    @RequestMapping(value ="/convocations/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Convocations> convocations = this.convocationDao.findByDeletedFalseOrderByIdDesc(pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.convocationDao.countConvocations();
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
            pages.setFirst_page_url(url_convocation_page+1);
            pages.setLast_page_url(url_convocation_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_convocation_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_convocation_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(convocations);
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
    @RequestMapping(value = "/convocations/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Convocations> convocations = this.convocationDao.recherche(s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.convocationDao.countRecherche(s);
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
            pages.setFirst_page_url(url_convocation_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_convocation_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_convocation_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_convocation_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(convocations);

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
    @RequestMapping(value ="/convocations/organisation/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @CurrentUser final UserDetailsImpl currentUser){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Long> events = this.evenementEquipeDao.selectByOrganisationIds(ids);
        List<Convocations> convocations = this.convocationDao.selectByEvenement(events, pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.convocationDao.countConvocations(events);
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
            pages.setFirst_page_url(url_convocation_org_page+1);
            pages.setLast_page_url(url_convocation_org_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_convocation_org_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_convocation_org_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(convocations);
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
    @RequestMapping(value = "/convocations/organisation/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(name="s") String s,
                                   @CurrentUser final UserDetailsImpl currentUser){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Long> events = this.evenementEquipeDao.selectByOrganisationIds(ids);
        List<Convocations> convocations = this.convocationDao.recherche(events, s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.convocationDao.countRecherche(events, s);
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
            pages.setFirst_page_url(url_convocation_org_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_convocation_org_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_convocation_org_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_convocation_org_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(convocations);

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
    @RequestMapping(value ="/convocations/evenement/page/{id}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @PathVariable(value = "id") Long id){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Convocations> convocations = this.convocationDao.selectByEvenement(evenement, pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.convocationDao.countConvocations(evenement);
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
            pages.setFirst_page_url(url_convocation_eq_page+id+"/"+1);
            pages.setLast_page_url(url_convocation_eq_page+id+"/"+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_convocation_eq_page+id+"/"+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_convocation_eq_page+id+"/"+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(convocations);
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
    @RequestMapping(value = "/convocations/evenement/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(value = "id") Long id,
                                   @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Convocations> convocations = this.convocationDao.recherche(evenement, s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.convocationDao.countRecherche(evenement, s);
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
            pages.setFirst_page_url(url_convocation_eq_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_convocation_eq_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_convocation_eq_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_convocation_eq_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(convocations);

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
