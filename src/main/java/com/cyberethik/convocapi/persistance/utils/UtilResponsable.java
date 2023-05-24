package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.EquipeDto;
import com.cyberethik.convocapi.persistance.dto.ResponsableDto;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilResponsable {

    public static ResponsableDto convertToDto(Responsables data, ModelMapper modelMapper) {
        ResponsableDto dto = modelMapper.map(data, ResponsableDto.class);
        return dto;
    }

    public static Responsables convertToEntity(ResponsableDto dto, ModelMapper modelMapper) throws ParseException {
        Responsables data = modelMapper.map(dto, Responsables.class);
        return data;
    }
}
