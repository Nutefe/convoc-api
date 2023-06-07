package com.cyberethik.convocapi.persistance.dto;

import com.cyberethik.convocapi.persistance.entities.Organisations;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author nutefe
 * Model des comptes
 */
@Data
public class AccountDto
{
    private Long id;
    private String email;
    private String password;
    private String avatar;
    private RoleDto role;
    private boolean emailVerified;
    private boolean actif;
    private Date emailVerifiedAt;
    private List<OrganisationDto> organistions = new ArrayList<>();
    private String createdAt;
//    public Date getSubmissionDateConverted(){
//        return Helpers.getDateFromString(this.created);
//    }
//    public void setSubmissionDate(Date date) {
//        this.created = Helpers.convertAllDate(date);
//    }
}
