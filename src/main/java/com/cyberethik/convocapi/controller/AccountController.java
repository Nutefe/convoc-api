package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.messaging.emails.service.EmailSenderService;
import com.cyberethik.convocapi.persistance.dto.AccountDto;
import com.cyberethik.convocapi.persistance.dto.OrganisationDto;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.persistance.utils.UtilAccount;
import com.cyberethik.convocapi.persistance.utils.UtilOrganisation;
import com.cyberethik.convocapi.playload.pages.AccountPage;
import com.cyberethik.convocapi.playload.request.AccountRequest;
import com.cyberethik.convocapi.playload.request.AccountUpdateRequest;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.playload.request.UpdatePassRequest;
import com.cyberethik.convocapi.playload.response.ApiMessage;
import com.cyberethik.convocapi.security.jwt.JwtUtils;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static com.cyberethik.convocapi.playload.helper.Helpers.sortByCreatedDesc;

@RestController
@RequestMapping({ "/web/service" })
public class AccountController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_account_page}")
    private String url_account_page;
    @Value("${app.url_account_search_page}")
    private String url_account_search_page;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RefreshTokenDao refreshTokenDao;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private OrganisationDao organisationDao;
    @Autowired
    private AccountOrganisationDao accountOrganisationDao;
    @Autowired
    private EquipeMembreDao equipeMembreDao;
    @Autowired
    private EquipeDao equipeDao;
    private final EmailSenderService emailSenderService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }
    @GetMapping({ "/account/me" })
    public Object getCurrentUser(@CurrentUser final UserDetailsImpl currentUser) {
        final Accounts account = this.accountDao.findById(currentUser.getId()).orElseThrow(() -> new RuntimeException("Error: user is not found."));
        List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
        List<OrganisationDto> organisationDtos = new ArrayList<>();

        organisations.forEach(organisation ->{
            Long membres = this.equipeMembreDao.countByOrganisation(organisation.getId());
            Long equipes = this.equipeDao.countByOrganisation(organisation.getId());
            OrganisationDto organisationDto = UtilOrganisation.convertToDto(organisation, modelMapper);
            organisationDto.setNbrEquipe(equipes);
            organisationDto.setNbrMembre(membres);
            organisationDtos.add(organisationDto);
        });
        AccountDto accountDto = UtilAccount.convertToDto(account, modelMapper);
        accountDto.setOrganistions(organisationDtos);
        return accountDto;
    }
    @RequestMapping(value = { "/password/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> accountUpdate(@Valid @RequestBody final UpdatePassRequest request,
                                         @PathVariable(value = "id") Long id) {
        Accounts account = this.accountDao.selectById(id);
        if (this.passwordEncoder.matches(request.getAncien(), account.getPassword())) {
            this.accountDao.updatePassword(account.getId(), request.getNouveau());
            return ResponseEntity.ok(new ApiMessage(HttpStatus.OK, "Mot de passe modifier avec succès"));
        }
        else {
            return ResponseEntity.internalServerError().body(new ApiMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur de modification ancien mot de passe incorrect"));
        }
    }

    @RequestMapping(value = { "/account/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> accountSave(@Valid @RequestBody final AccountRequest request) throws MessagingException {

        if (accountDao.existsByEmail(request.getEmail())){
            return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error email already exists", "Error duplication key"));
        }

        String slug;
        do {
            int length = 10;
            boolean useLetters = true;
            boolean useNumbers = true;
            slug = RandomStringUtils.random(length, useLetters, useNumbers);
        }while (this.accountDao.existsBySlug(slug));

        final Accounts account = new Accounts();
        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword());
        account.setLibelle(request.getLibelle());
        account.setRole(this.roleDao.selectById(request.getRole().getId()));
        account.setActif(true);
        account.setActifAt(new Date());
        account.setEmailVerified(true);
        account.setEmailVerifiedAt(new Date());
        account.setSlug(slug);

        Accounts accountSave = this.accountDao.save(account);
        if (request.getOrganisation()!=null && accountSave!=null){

            AccountOrganisations accountOrganisation = new AccountOrganisations();
            Organisations organisation = this.organisationDao.selectById(request.getOrganisation().getId());
            AccountOrganisationPK pk = new AccountOrganisationPK(accountSave, organisation);
            accountOrganisation.setId(pk);
            this.accountOrganisationDao.save(accountOrganisation);
        }
        return ResponseEntity.ok(accountSave);
    }
    @RequestMapping(value = { "/account/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> accountUpdate(@Valid @RequestBody final AccountUpdateRequest request,
                                         @PathVariable(value = "id") Long id) throws MessagingException {

        if (accountDao.existsByEmail(request.getEmail(), id)){
            return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error email already exists", "Error duplication key"));
        }

        final Accounts account = this.accountDao.selectById(id);
        account.setEmail(request.getEmail());
        account.setLibelle(request.getLibelle());
        if (request.getRole() != null)
            account.setRole(this.roleDao.selectById(request.getRole().getId()));

        Accounts accountSave = this.accountDao.update(account);
        if (request.getOrganisation()!=null && accountSave!=null){
            List<AccountOrganisations> organisations = this.accountOrganisationDao.selectByAccountAll(accountSave);
            if (organisations.size()>0){
                organisations.forEach( item -> {
                    this.accountOrganisationDao.delete(item);
                });
            }
            AccountOrganisations accountOrganisation = new AccountOrganisations();
            Organisations organisation = this.organisationDao.selectById(request.getOrganisation().getId());
            AccountOrganisationPK pk = new AccountOrganisationPK(accountSave, organisation);
            accountOrganisation.setId(pk);
            this.accountOrganisationDao.save(accountOrganisation);
        }
        return ResponseEntity.ok(accountSave);
    }
    @RequestMapping(value = { "/account/delete/{id}" }, method = { RequestMethod.DELETE })
    public void accountDelete(@PathVariable(value = "id") Long id) throws MessagingException {
        final Accounts account = this.accountDao.selectById(id);
        this.accountDao.delete(account);
    }

    @RequestMapping(value = { "/accounts/delete" }, method = { RequestMethod.DELETE })
    public void accountDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long account :
                    request.getIds()) {
                Accounts accountUp = this.accountDao.selectById(account);
                this.accountDao.delete(accountUp);
            }
        }
    }
    @RequestMapping(value = { "/accounts" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<Accounts> selectAll() {
        return this.accountDao.findByDeletedFalseOrderByIdDesc();
    }
    @RequestMapping(value ="/accounts/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public AccountPage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Accounts> accounts = this.accountDao.findByDeletedFalseOrderByIdDesc(pageable);
        List<AccountDto> accountDtos = new ArrayList<>();

        accounts.forEach(account ->{
            List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
            List<OrganisationDto> organisationDtos = new ArrayList<>();

            organisations.forEach(organisation ->{
                Long membres = this.equipeMembreDao.countByOrganisation(organisation.getId());
                Long equipes = this.equipeDao.countByOrganisation(organisation.getId());
                OrganisationDto organisationDto = UtilOrganisation.convertToDto(organisation, modelMapper);
                organisationDto.setNbrEquipe(equipes);
                organisationDto.setNbrMembre(membres);
                organisationDtos.add(organisationDto);
            });

            AccountDto accountDto = UtilAccount.convertToDto(account, modelMapper);
            accountDto.setOrganistions(organisationDtos);
            accountDtos.add(accountDto);
        });

        AccountPage pages = new AccountPage();

        Long total = this.accountDao.countAccounts();
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
            pages.setFirst_page_url(url_account_page+1);
            pages.setLast_page_url(url_account_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_account_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_account_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(accountDtos);
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
    @RequestMapping(value = "/accounts/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public AccountPage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Accounts> accounts = this.accountDao.recherche(s, pageable);
        List<AccountDto> accountDtos = new ArrayList<>();

        accounts.forEach(account ->{
            List<Organisations> organisations = this.accountOrganisationDao.selectByAccount(account);
            List<OrganisationDto> organisationDtos = new ArrayList<>();

            organisations.forEach(organisation ->{
                Long membres = this.equipeMembreDao.countByOrganisation(organisation.getId());
                Long equipes = this.equipeDao.countByOrganisation(organisation.getId());
                OrganisationDto organisationDto = UtilOrganisation.convertToDto(organisation, modelMapper);
                organisationDto.setNbrEquipe(equipes);
                organisationDto.setNbrMembre(membres);
                organisationDtos.add(organisationDto);
            });
            AccountDto accountDto = UtilAccount.convertToDto(account, modelMapper);
            accountDto.setOrganistions(organisationDtos);
            accountDtos.add(accountDto);
        });
        AccountPage pages = new AccountPage();
        Long total = this.accountDao.countRecherche(s);
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
            pages.setFirst_page_url(url_account_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_account_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_account_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_account_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(accountDtos);

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
