package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.persistance.dto.MembreDto;
import com.cyberethik.convocapi.persistance.dto.OrganisationDto;
import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.persistance.utils.UtilMembre;
import com.cyberethik.convocapi.persistance.utils.UtilOrganisation;
import com.cyberethik.convocapi.playload.pages.ResponsePage;
import com.cyberethik.convocapi.playload.request.LongRequest;
import com.cyberethik.convocapi.security.services.CurrentUser;
import com.cyberethik.convocapi.security.services.UserDetailsImpl;
import com.cyberethik.convocapi.storage.FileSystemStorageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cyberethik.convocapi.playload.helper.Helpers.sortByCreatedDesc;

@RestController
@RequestMapping({ "/web/service" })
public class OrganisationController {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${app.page_size}")
    private Integer page_size;
    @Value("${app.base}")
    private String path;
    @Value("${app.url_organisation_page}")
    private String url_organisation_page;
    @Value("${app.url_organisation_search_page}")
    private String url_organisation_search_page;
    @Autowired
    private OrganisationDao organisationDao;
    @Autowired
    private AccountOrganisationDao accountOrganisationDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private EquipeMembreDao equipeMembreDao;
    @Autowired
    private EquipeDao equipeDao;
    @Autowired
    private FileSystemStorageService fileStorageService;
    @Autowired
    private FileDao fileDao;
    public OrganisationController() {
    }

    @RequestMapping(value = { "/organisation/save" }, method = { RequestMethod.POST })
    public ResponseEntity<?> organisationSave(
            @RequestParam(value = "nom", required = false)  String nom,
            @RequestParam(value = "desciption", required = false)  String desciption,
            @RequestParam(value = "devise", required = false)  String devise,
            @RequestParam(value = "logo", required = false) MultipartFile logo) throws IOException {

        Organisations organisation = new Organisations();
        organisation.setNom(nom);
        organisation.setDesciption(desciption);
        organisation.setDevise(devise);

        if (logo != null ) {
            if (!logo.isEmpty()){
                this.fileStorageService.store(logo);
                if (this.fileStorageService.exist(logo.getOriginalFilename())) {
                    String s2 = this.fileStorageService.rename(logo.getOriginalFilename(), Long.valueOf(this.fileDao.counts()+1));
                    Files p =this.fileDao.save(new Files(s2));
                    organisation.setLogo(p.getName());
                }
            }
        }
        return ResponseEntity.ok(this.organisationDao.save(organisation));
    }
    @RequestMapping(value = { "/organisation/update/{id}" }, method = { RequestMethod.PUT })
    public ResponseEntity<?> organisationUpdate(
            @RequestParam(value = "nom", required = false)  String nom,
            @RequestParam(value = "desciption", required = false)  String desciption,
            @RequestParam(value = "devise", required = false)  String devise,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @PathVariable(value = "id") Long id) throws IOException {

        Organisations organisation = this.organisationDao.selectById(id);
        organisation.setNom(nom);
        organisation.setDesciption(desciption);
        organisation.setDevise(devise);

        if (logo != null ) {
            if (!logo.isEmpty()){
                this.fileStorageService.store(logo);
                if (this.fileStorageService.exist(logo.getOriginalFilename())) {
                    String s2 = this.fileStorageService.rename(logo.getOriginalFilename(), Long.valueOf(this.fileDao.counts()+1));
                    Files p =this.fileDao.save(new Files(s2));
                    organisation.setLogo(p.getName());
                }
            }
        }

        Organisations organisationSave = this.organisationDao.save(organisation);

        Long membres = this.equipeMembreDao.countByOrganisation(organisationSave.getId());
        Long equipes = this.equipeDao.countByOrganisation(organisationSave.getId());
        OrganisationDto organisationDto = UtilOrganisation.convertToDto(organisationSave, modelMapper);
        organisationDto.setNbrEquipe(equipes);
        organisationDto.setNbrMembre(membres);

        return ResponseEntity.ok(organisationDto);
    }
    @RequestMapping(value = { "/organisation/delete/{id}" }, method = { RequestMethod.DELETE })
    public void organisationUpdate(@PathVariable(value = "id") Long id) throws MessagingException {
        final Organisations organisation = this.organisationDao.selectById(id);
        this.organisationDao.delete(organisation);
    }
    @RequestMapping(value = { "/organisations/delete" }, method = { RequestMethod.DELETE })
    public void organisationDelete(@Valid @RequestBody final LongRequest request) {
        if (request.getIds() != null && request.getIds().size() > 0){
            for (Long id :
                    request.getIds()) {
                Organisations organisation = this.organisationDao.selectById(id);
                this.organisationDao.delete(organisation);
            }
        }
    }
    @RequestMapping(value = { "/organisations" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<OrganisationDto> selectAll() {
        List<Organisations> organisations = this.organisationDao.findByDeletedFalseOrderByIdDesc();

        List<OrganisationDto> organisationDtos = new ArrayList<>();

        organisations.forEach(organisation ->{
            Long membres = this.equipeMembreDao.countByOrganisation(organisation.getId());
            Long equipes = this.equipeDao.countByOrganisation(organisation.getId());
            OrganisationDto organisationDto = UtilOrganisation.convertToDto(organisation, modelMapper);
            organisationDto.setNbrEquipe(equipes);
            organisationDto.setNbrMembre(membres);
            organisationDtos.add(organisationDto);
        });
        return organisationDtos;
    }
    @RequestMapping(value = { "/organisations/user" }, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.OK)
    public List<OrganisationDto> selectAll(@CurrentUser final UserDetailsImpl currentUser) {
        Accounts account = this.accountDao.selectById(currentUser.getId());

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
        return organisationDtos;
    }
    @RequestMapping(value ="/organisations/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePage selectPage(@PathVariable(value = "page") int page) {

        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());

        List<Organisations> organisations = this.organisationDao.findByDeletedFalseOrderByIdDesc(pageable);

        List<OrganisationDto> organisationDtos = new ArrayList<>();

        organisations.forEach(organisation ->{
            Long membres = this.equipeMembreDao.countByOrganisation(organisation.getId());
            Long equipes = this.equipeDao.countByOrganisation(organisation.getId());
            OrganisationDto organisationDto = UtilOrganisation.convertToDto(organisation, modelMapper);
            organisationDto.setNbrEquipe(equipes);
            organisationDto.setNbrMembre(membres);
            organisationDtos.add(organisationDto);
        });

        ResponsePage pages = new ResponsePage();

        Long total = this.organisationDao.countOrganisations();
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
            pages.setFirst_page_url(url_organisation_page+1);
            pages.setLast_page_url(url_organisation_page+lastPage);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_organisation_page+(page+1));
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_organisation_page+(page-1));
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }
            pages.setPath(path);
            pages.setData(organisationDtos);
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
    @RequestMapping(value = "/organisations/search/page", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponsePage searchPage(@RequestParam(name="page") int page,
                                     @RequestParam(name="s") String s){
        Pageable pageable = PageRequest.of(page - 1, page_size, sortByCreatedDesc());
        List<Organisations> organisations = this.organisationDao.recherche(s, pageable);
        List<OrganisationDto> organisationDtos = new ArrayList<>();

        organisations.forEach(organisation ->{
            Long membres = this.equipeMembreDao.countByOrganisation(organisation.getId());
            Long equipes = this.equipeDao.countByOrganisation(organisation.getId());
            OrganisationDto organisationDto = UtilOrganisation.convertToDto(organisation, modelMapper);
            organisationDto.setNbrEquipe(equipes);
            organisationDto.setNbrMembre(membres);
            organisationDtos.add(organisationDto);
        });
        ResponsePage pages = new ResponsePage();
        Long total = this.organisationDao.countRecherche(s);
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
            pages.setFirst_page_url(url_organisation_search_page+"?page="+1+"&s="+s);
            pages.setLast_page_url(url_organisation_search_page+"?page="+lastPage+"&s="+s);
            if (page >= lastPage){
                pages.setNext_page_url("");
            }else {
                pages.setNext_page_url(url_organisation_search_page+"?page="+(page+1)+"&s="+s);
            }

            if (page == 1){
                pages.setPrev_page_url("");
                pages.setFrom(1L);
                pages.setTo(Long.valueOf(page_size));
            } else {
                pages.setPrev_page_url(url_organisation_search_page+"?page="+(page-1)+"&s="+s);
                pages.setFrom(1L + (Long.valueOf(page_size)*(page -1)));
                pages.setTo(Long.valueOf(page_size) * page);
            }

            pages.setPath(path);
            pages.setData(organisationDtos);

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
