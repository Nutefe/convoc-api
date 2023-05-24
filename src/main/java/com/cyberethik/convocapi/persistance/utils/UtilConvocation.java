package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.ConvocationDto;
import com.cyberethik.convocapi.persistance.dto.OrganistionDto;
import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilConvocation {

    public static ConvocationDto convertToDto(Convocations data, ModelMapper modelMapper) {
        ConvocationDto dto = modelMapper.map(data, ConvocationDto.class);
        return dto;
    }

    public static Convocations convertToEntity(ConvocationDto dto, ModelMapper modelMapper) throws ParseException {
        Convocations data = modelMapper.map(dto, Convocations.class);
        return data;
    }
}
