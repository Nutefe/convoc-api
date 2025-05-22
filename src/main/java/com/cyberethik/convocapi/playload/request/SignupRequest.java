package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class SignupRequest {
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;
  private Roles role;
  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

}
