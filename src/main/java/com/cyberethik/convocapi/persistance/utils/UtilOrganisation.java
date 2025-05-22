package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.OrganisationDto;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilOrganisation {

    public static OrganisationDto convertToDto(Organisations data, ModelMapper modelMapper) {
        OrganisationDto dto = modelMapper.map(data, OrganisationDto.class);
        return dto;
    }

    public static Organisations convertToEntity(OrganisationDto dto, ModelMapper modelMapper) throws ParseException {
        Organisations data = modelMapper.map(dto, Organisations.class);
        return data;
    }
}
