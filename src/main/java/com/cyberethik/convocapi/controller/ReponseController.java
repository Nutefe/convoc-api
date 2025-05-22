package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.messaging.emails.model.Email;
import com.cyberethik.convocapi.messaging.emails.service.EmailSenderService;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.playload.helper.Helpers;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.ConvocationRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.playload.request.ReponseRequest;
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
import java.util.*;

import static com.cyberethik.convocapi.playload.helper.Helpers.sortByCreatedDesc;

@RestController
@RequestMapping({ "/web/service" })
public class ReponseController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_reponse_page}")
    private String url_reponse_page;
    @Value("${app.url_reponse_search_page}")
    private String url_reponse_search_page;
    @Value("${app.url_reponse_org_page}")
    private String url_reponse_org_page;
    @Value("${app.url_reponse_org_search_page}")
    private String url_reponse_org_search_page;
    @Value("${app.url_reponse_eq_page}")
    private String url_reponse_eq_page;
    @Value("${app.url_reponse_eq_search_page}")
    private String url_reponse_eq_search_page;
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
    private final EmailSenderService emailSenderService;
    public ReponseController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @RequestMapping(value = { "/reponse/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> reponseSave(@Valid @RequestBody final ReponseRequest request) throws MessagingException {
        Convocations convocation = this.convocationDao.selectBySlug(request.getSlug());
        if (convocation == null){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, "Cette convocation n'existe pas", "Access denied"));
        }
        Reponses reponseInit = this.reponseDao.findTop1(convocation);
        if (reponseInit!=null){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, "Vous avez déjà répondu à cette convocation", "Access denied"));
        }

        Reponses reponse = new Reponses();
        reponse.setChoix(request.getChoix());
        reponse.setDescription(request.getDescription());
        reponse.setAlerte(request.isAlerte());
        reponse.setDateEnvoi(new Date());
        reponse.setConvocation(convocation);
        return ResponseEntity.ok(this.reponseDao.save(reponse));
    }
    @RequestMapping(value = { "/reponse/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> reponseUpdate(@Valid @RequestBody final ReponseRequest request,
                                          @PathVariable(value = "id") Long id) throws MessagingException {
        Reponses reponse = this.reponseDao.selectById(id);
        reponse.setChoix(request.getChoix());
        reponse.setDescription(request.getDescription());
        reponse.setAlerte(request.isAlerte());
        reponse.setDateEnvoi(new Date());
        reponse.setConvocation(this.convocationDao.selectBySlug(request.getSlug()));
        return ResponseEntity.ok(this.reponseDao.save(reponse));
    }
    
    @RequestMapping(value = { "/reponse/delete/{id}" }, method = { RequestMethod.DELETE })
    public void reponseUpdate(@PathVariable(value = "id") Long id) throws MessagingException {
        final Reponses reponse = this.reponseDao.selectById(id);
        this.reponseDao.delete(reponse);
    }

    @RequestMapping(value = { "/reponses/delete" }, method = { RequestMethod.DELETE })
    public void reponseDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long id :
                    request.getIds()) {
                Reponses reponse = this.reponseDao.selectById(id);
                this.reponseDao.delete(reponse);
            }
        }
    }
    @RequestMapping(value = { "/reponses/evenement/{id}" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Reponses> selectAll(@PathVariable(value = "id") Long id) {
        Evenements evenement = this.evenementDao.selectById(id);
        return this.reponseDao.selectByEvenement(evenement);
    }
    @RequestMapping(value = { "/reponses" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Reponses> selectAll() {
        return this.reponseDao.findByDeletedFalseOrderByIdDesc();
    }
    
    @RequestMapping(value ="/reponses/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Reponses> reponses = this.reponseDao.findByDeletedFalseOrderByIdDesc(pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.reponseDao.countReponses();
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
            pages.setFirst_page_url(url_reponse_page+1);
            pages.setLast_page_url(url_reponse_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_reponse_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_reponse_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(reponses);
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
    @RequestMapping(value = "/reponses/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Reponses> reponses = this.reponseDao.recherche(s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.reponseDao.countRecherche(s);
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
            pages.setFirst_page_url(url_reponse_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_reponse_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_reponse_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_reponse_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(reponses);

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
    @RequestMapping(value ="/reponses/organisation/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @CurrentUser final UserDetailsImpl currentUser){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Long> events = this.evenementEquipeDao.selectByOrganisationIds(ids);
        List<Reponses> reponses = this.reponseDao.selectByEvenement(events, pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.reponseDao.countReponses(events);
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
            pages.setFirst_page_url(url_reponse_org_page+1);
            pages.setLast_page_url(url_reponse_org_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_reponse_org_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_reponse_org_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(reponses);
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
    @RequestMapping(value = "/reponses/organisation/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                   @RequestParam(name="s") String s,
                                   @CurrentUser final UserDetailsImpl currentUser){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Long> ids = this.accountOrganisationDao.selectByAccountIds(account);
        List<Long> events = this.evenementEquipeDao.selectByOrganisationIds(ids);
        List<Reponses> reponses = this.reponseDao.recherche(events, s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.reponseDao.countRecherche(events, s);
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
            pages.setFirst_page_url(url_reponse_org_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_reponse_org_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_reponse_org_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_reponse_org_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(reponses);

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
    @RequestMapping(value ="/reponses/evenement/page/{id}/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page,
                                   @PathVariable(value = "id") Long id){

        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Reponses> reponses = this.reponseDao.selectByEvenement(evenement, pageable);

        ResponsePage pages = new ResponsePage();

        Long total = this.reponseDao.countReponses(evenement);
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
            pages.setFirst_page_url(url_reponse_eq_page+id+"/"+1);
            pages.setLast_page_url(url_reponse_eq_page+id+"/"+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_reponse_eq_page+id+"/"+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_reponse_eq_page+id+"/"+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(reponses);
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
    @RequestMapping(value = "/reponses/evenement/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(
                                    @RequestParam(name="page") int page,
                                    @RequestParam(value = "id") Long id,
                                   @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size);
        final Evenements evenement = this.evenementDao.selectById(id);
        List<Reponses> reponses = this.reponseDao.recherche(evenement, s, pageable);

        ResponsePage pages = new ResponsePage();
        Long total = this.reponseDao.countRecherche(evenement, s);
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
            pages.setFirst_page_url(url_reponse_eq_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_reponse_eq_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_reponse_eq_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_reponse_eq_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(reponses);

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

    @RequestMapping(value = { "/status/reponse/encours" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> countMonth(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.selectById(currentUser.getId());
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
        Organisations organisationInit = organisations.get(0);
        return ResponseEntity.ok(this.reponseDao.countReponse(organisationInit.getId(), new Date()));
    }
}
