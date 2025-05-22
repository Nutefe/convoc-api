package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.FileDto;
import com.cyberethik.convocapi.persistance.entities.Files;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilFile {

    public static FileDto convertToDto(Files file, ModelMapper modelMapper) {
        FileDto fileDto = modelMapper.map(file, FileDto.class);
        fileDto.setSubmissionDate(file.getCreatedAt());
        return fileDto;
    }

    public static Files convertToEntity(FileDto fileDto, ModelMapper modelMapper) throws ParseException {
        Files file = modelMapper.map(fileDto, Files.class);
        file.setCreatedAt(fileDto.getSubmissionDateConverted());
        return file;
    }
}
