package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.ReponseDto;
import com.cyberethik.convocapi.persistance.dto.ResponsableDto;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilReponse {

    public static ReponseDto convertToDto(Reponses data, ModelMapper modelMapper) {
        ReponseDto dto = modelMapper.map(data, ReponseDto.class);
        return dto;
    }

    public static Reponses convertToEntity(ReponseDto dto, ModelMapper modelMapper) throws ParseException {
        Reponses data = modelMapper.map(dto, Reponses.class);
        return data;
    }
}
