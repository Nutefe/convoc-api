package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.entities.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class AccountUpdateRequest {
  private String email;
  private String libelle;
  private Roles role;
  private Organisations organisation;

}
