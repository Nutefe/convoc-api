package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.EquipeDto;
import com.cyberethik.convocapi.persistance.dto.EvenementDto;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilEvenement {

    public static EvenementDto convertToDto(Evenements data, ModelMapper modelMapper) {
        EvenementDto dto = modelMapper.map(data, EvenementDto.class);
        return dto;
    }

    public static Evenements convertToEntity(EvenementDto dto, ModelMapper modelMapper) throws ParseException {
        Evenements data = modelMapper.map(dto, Evenements.class);
        return data;
    }
}
