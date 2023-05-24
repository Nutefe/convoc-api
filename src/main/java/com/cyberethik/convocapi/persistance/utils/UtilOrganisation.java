package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.AccountDto;
import com.cyberethik.convocapi.persistance.dto.OrganistionDto;
import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilOrganisation {

    public static OrganistionDto convertToDto(Organisations data, ModelMapper modelMapper) {
        OrganistionDto dto = modelMapper.map(data, OrganistionDto.class);
        return dto;
    }

    public static Organisations convertToEntity(OrganistionDto dto, ModelMapper modelMapper) throws ParseException {
        Organisations data = modelMapper.map(dto, Organisations.class);
        return data;
    }
}
