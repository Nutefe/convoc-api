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
public class ResponsableDto
{
    private Long id;
    private String email;
    private String libelle;
    private String telephone;
    private String adresse;
    private Date createdAt;
}
