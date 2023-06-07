package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.exception.ApiError;
import com.cyberethik.convocapi.exception.TokenRefreshException;
import com.cyberethik.convocapi.messaging.emails.model.Email;
import com.cyberethik.convocapi.messaging.emails.service.EmailSenderService;
import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.RefreshTokens;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import com.cyberethik.convocapi.persistance.service.dao.*;
import com.cyberethik.convocapi.playload.helper.Helpers;
import com.cyberethik.convocapi.playload.request.LoginRequest;
import com.cyberethik.convocapi.playload.request.ReponseRequest;
import com.cyberethik.convocapi.playload.response.ApiMessage;
import com.cyberethik.convocapi.playload.response.JwtResponse;
import com.cyberethik.convocapi.playload.response.TokenRefreshResponse;
import com.cyberethik.convocapi.security.jwt.JwtUtils;
import com.cyberethik.convocapi.security.services.UserDetailsImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("/web/service")
public class AuthController {
  @Value("${spring.mail.username}")
  private String sender;
  @Autowired
  private ModelMapper modelMapper;
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
  private ConvocationDao convocationDao;
  @Autowired
  private ReponseDao reponseDao;
  private final EmailSenderService emailSenderService;
  public AuthController(EmailSenderService emailSenderService) {
    this.emailSenderService = emailSenderService;
  }

  @PostMapping("/auth/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
    Accounts accExist = this.accountDao.selectByEmail(loginRequest.getEmail());
    if (!accExist.isActif()){
      return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Votre compte n'est pas actif", "Erreur compte non actif"));
    }

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Accounts account = accountDao.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("Error: Profil is not found."));

    final RefreshTokens refreshTokens = this.refreshTokenDao.createRefreshToken(userDetails.getEmail());
    return ResponseEntity.ok(new JwtResponse(jwt, refreshTokens.getToken(), account));
  }

  @PostMapping({ "/auth/refreshtoken" })
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody final TokenRefreshResponse request, HttpServletRequest servletRequest) {
    String requestRefreshToken = request.getRefreshToken();

    return this.refreshTokenDao.findByToken(requestRefreshToken)
            .map(refreshTokenDao::verifyExpiration)
            .map(RefreshTokens::getAccount)
            .map(user -> {
              String token = jwtUtils.generateJwtEmail(user.getEmail());

              Accounts account = accountDao.findById(user.getId()).orElseThrow(() -> new RuntimeException("Error: Profil is not found."));

              return ResponseEntity.ok(new JwtResponse(token, requestRefreshToken, account));
            })
            .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not in database!"));
  }

  @RequestMapping(value = { "/auth/forgot/password" }, method = { RequestMethod.GET })
  public void forgotPassword(@RequestParam(name="email") String email) throws MessagingException {
    Accounts accounts = this.accountDao.selectByEmail(email);
    if (accounts!=null){
      Email emailInit = new Email();
      emailInit.setTo(accounts.getEmail());
      emailInit.setFrom(sender);
      emailInit.setSubject("Reinitialise votre mot de passe ");
      emailInit.setTemplate("email-forgot-password.html");
      Map<String, Object> properties = new HashMap<>();
      properties.put("name", accounts.getLibelle());
      properties.put("link", Helpers.base_client_url+"reset/password/"+accounts.getSlug());
      emailInit.setProperties(properties);
      emailSenderService.sendHtmlMessage(emailInit);
    }
  }

  @RequestMapping(value = { "/auth/email/send" }, method = { RequestMethod.GET })
  public void sendMail(@RequestParam(name="email") String email) throws MessagingException {
    Accounts accounts = this.accountDao.selectByEmail(email);
    if (accounts!=null){
      Email emailInit = new Email();
      emailInit.setTo(accounts.getEmail());
      emailInit.setFrom(sender);
      emailInit.setSubject("Validation de l’adresse e-mail ");
      emailInit.setTemplate("email-forgot-password.html");
      Map<String, Object> properties = new HashMap<>();
      properties.put("name", accounts.getLibelle());
      properties.put("link", Helpers.base_client_url+"reset/password/"+accounts.getSlug());
      emailInit.setProperties(properties);
      emailSenderService.sendHtmlMessage(emailInit);
    }
  }

  @RequestMapping(value = { "/auth/reponse/save" }, method = { RequestMethod.POST })
  public ResponseEntity<?> reponseSave(@Valid @RequestBody final ReponseRequest request) throws MessagingException {
    Reponses reponse = new Reponses();
    reponse.setChoix(request.getChoix());
    reponse.setDescription(request.getDescription());
    reponse.setAlerte(request.isAlerte());
    reponse.setDateEnvoi(new Date());
    reponse.setConvocation(this.convocationDao.selectBySlug(request.getSlug()));
    return ResponseEntity.ok(this.reponseDao.save(reponse));
  }

//  @RequestMapping(value = { "/auth/candidat/signin/up" }, method = { RequestMethod.POST })
//  public ResponseEntity<?> signinUpAllCheck(@Valid @RequestBody final CandidatRequest request) throws MessagingException {
//
//    if (accountDao.existsByEmail(request.getEmail())){
//      return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error email already exists", "Error duplication key"));
//    }
//    if (!request.getCode().isBlank() && request.getCode() != null){
//      if (!codeDao.existsByCode(request.getCode())){
//        return  ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error code does not exists", "Not found"));
//      }
//    }
//
//    final Candidats candidat = new Candidats();
//    candidat.setUsername("nom-"+this.candidatDao.counts());
//    candidat.setEmail(request.getEmail());
//    candidat.setPassword(request.getPassword());
//    candidat.setExperience(this.experienceDao.selectById(request.getExperience().getId()));
//    candidat.setStudyLevel(this.studyLevelDao.selectById(request.getStudyLevel().getId()));
//    candidat.setRegion(this.regionDao.selectById(request.getRegion().getId()));
//    candidat.setFirstName(request.getFirstName());
//    candidat.setLastName(request.getLastName());
//    candidat.setPhoneNumber(request.getPhoneNumber());
//    candidat.setSearchingApprenticeships(request.isSearchingApprenticeships());
//    candidat.setSearchingInternships(request.isSearchingInternships());
//    candidat.setSearchingJobs(request.isSearchingJobs());
//    candidat.setRole(this.roleDao.selectById(3));
//
//    if (!request.getCode().isBlank() && request.getCode() != null){
//      candidat.setTypeCandidat(this.typeCandidatDao.selectById(2));
//      Set<Codes> codes = new HashSet<>();
//      codes.add(this.codeDao.selectByCode(request.getCode()));
//      candidat.setCodes(codes);
//    } else {
//      candidat.setTypeCandidat(this.typeCandidatDao.selectById(1));
//    }
//
//    Candidats cadidatSave = this.candidatDao.save(candidat);
//    if (cadidatSave!=null){
//      Email email = new Email();
//      email.setTo(cadidatSave.getEmail());
//      email.setFrom(sender);
//      email.setSubject("Validation de l’adresse e-mail ");
//      email.setTemplate("verify-email.html");
//      Map<String, Object> properties = new HashMap<>();
//      properties.put("name", cadidatSave.getFirstName()+" "+cadidatSave.getLastName());
//      properties.put("link", Helpers.base_client_url+"espace-candidat/verify/email/"+cadidatSave.getUsername());
//      email.setProperties(properties);
//      emailSenderService.sendHtmlMessage(email);
//    }
//    return ResponseEntity.ok(cadidatSave);
//  }

//  @RequestMapping(value = { "/auth/email/account/enable" }, method = { RequestMethod.GET })
//  public ResponseEntity<?> candidatEnable(@RequestParam(name="username") final String username) {
//
//    Candidats accountUp = this.candidatDao.selectByUsername(username);
////    Accounts accountUp = this.accountDao.findById(id).orElseThrow(() -> new RuntimeException("Error: account is not found."));
//    accountUp.setActif(true);
//    accountUp.setEmailVerified(true);
//    accountUp.setEmailVerifiedAt(new Date());
//    accountUp.setActifAt(new Date());
//    this.accountDao.update(accountUp);
//    return ResponseEntity.ok(new ApiMessage(HttpStatus.OK, "Accounts enable successfully"));
//  }
//  @RequestMapping(value = { "/auth/email/send" }, method = { RequestMethod.GET })
//  public void candidatMail(@RequestParam(name="email") String email) throws MessagingException {
//    Accounts accounts = this.accountDao.selectByEmail(email);
//    if (accounts!=null){
//      Candidats cadidatSave = this.candidatDao.selectById(accounts.getId());
//      Email emailInit = new Email();
//      emailInit.setTo(cadidatSave.getEmail());
//      emailInit.setFrom(sender);
//      emailInit.setSubject("Validation de l’adresse e-mail ");
//      emailInit.setTemplate("verify-email.html");
//      Map<String, Object> properties = new HashMap<>();
//      properties.put("name", cadidatSave.getFirstName()+" "+cadidatSave.getLastName());
//      properties.put("link", Helpers.base_client_url+"espace-candidat/verify/email/"+cadidatSave.getUsername());
//      emailInit.setProperties(properties);
//      emailSenderService.sendHtmlMessage(emailInit);
//    }
//  }

  @RequestMapping(value = { "/auth/check/email/exist" }, method = { RequestMethod.GET })
  @ResponseStatus(HttpStatus.OK)
  public Boolean checkEmail(@RequestParam(name="email") String email) {
    return this.accountDao.existsByEmail(email);
  }

}
