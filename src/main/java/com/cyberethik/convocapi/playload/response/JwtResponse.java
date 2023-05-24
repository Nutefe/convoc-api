package com.cyberethik.convocapi.playload.response;

import lombok.Data;

@Data
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Object account;
  private String refreshToken;

  public JwtResponse(String token, Object account) {
    this.token = token;
    this.account = account;
  }

  public JwtResponse(String token, String refreshToken, Object account) {
    this.token = token;
    this.refreshToken = refreshToken;
    this.account = account;
  }

}
