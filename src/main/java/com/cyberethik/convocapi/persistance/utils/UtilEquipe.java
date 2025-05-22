package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.ConvocationDto;
import com.cyberethik.convocapi.persistance.dto.EquipeDto;
import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilEquipe {

    public static EquipeDto convertToDto(Equipes data, ModelMapper modelMapper) {
        EquipeDto dto = modelMapper.map(data, EquipeDto.class);
        return dto;
    }

    public static Equipes convertToEntity(EquipeDto dto, ModelMapper modelMapper) throws ParseException {
        Equipes data = modelMapper.map(dto, Equipes.class);
        return data;
    }
}
