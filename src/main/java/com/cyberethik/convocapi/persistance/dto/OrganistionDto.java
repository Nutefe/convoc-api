package com.cyberethik.convocapi.persistance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author nutefe
 * Model des comptes
 */
@Data
public class OrganistionDto
{
    private Long id;
    private String nom;
    private String desciption;
    private String devise;
    private String logo;
    private Date createdAt;
}
