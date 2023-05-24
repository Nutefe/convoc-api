package com.cyberethik.convocapi.playload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LongRequest {
  private List<Long> ids;

}
