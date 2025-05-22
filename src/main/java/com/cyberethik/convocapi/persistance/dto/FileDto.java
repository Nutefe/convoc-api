package com.cyberethik.convocapi.persistance.dto;

import com.cyberethik.convocapi.playload.helper.Helpers;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author nutefe
 * Model des fichiers
 */
@Data
public class FileDto
{
    private Long id;
    private String name;
    private String created;
    public Date getSubmissionDateConverted(){
        return Helpers.getDateFromString(this.created);
    }
    public void setSubmissionDate(Date date) {
        this.created = Helpers.convertAllDate(date);
    }
}
