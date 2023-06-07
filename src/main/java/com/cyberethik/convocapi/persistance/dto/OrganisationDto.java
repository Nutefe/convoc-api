package com.cyberethik.convocapi.persistance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author nutefe
 * Model des comptes
 */
@Data
public class OrganisationDto
{
    private Long id;
    private String nom;
    private String desciption;
    private String devise;
    private String logo;
    private Long nbrEquipe;
    private Long nbrMembre;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
}
