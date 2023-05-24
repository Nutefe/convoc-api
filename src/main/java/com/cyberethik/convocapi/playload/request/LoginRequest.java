package com.cyberethik.convocapi.playload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class LoginRequest {
	@NotBlank
  	private String email;
	@NotBlank
	private String password;

}
