package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.entities.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class AccountRequest {
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;
  @NotBlank
  @Size(min = 6, max = 40)
  private String password;
  @NotBlank
  private String libelle;
  private Roles role;
  private Organisations organisation;

}
