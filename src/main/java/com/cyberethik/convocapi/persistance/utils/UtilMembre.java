package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.EquipeDto;
import com.cyberethik.convocapi.persistance.dto.MembreDto;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilMembre {

    public static MembreDto convertToDto(Membres data, ModelMapper modelMapper) {
        MembreDto dto = modelMapper.map(data, MembreDto.class);
        return dto;
    }

    public static Membres convertToEntity(MembreDto dto, ModelMapper modelMapper) throws ParseException {
        Membres data = modelMapper.map(dto, Membres.class);
        return data;
    }
}
