package com.cyberethik.convocapi.persistance.utils;


import com.cyberethik.convocapi.persistance.dto.RoleDto;
import com.cyberethik.convocapi.persistance.entities.Roles;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilRole {

    public static RoleDto convertToDto(Roles role, ModelMapper modelMapper) {
        RoleDto roleDto = modelMapper.map(role, RoleDto.class);
//        roleDto.setSubmissionDate(role.getCreatedAt());
        return roleDto;
    }

    public static Roles convertToEntity(RoleDto roleDto, ModelMapper modelMapper) throws ParseException {
        Roles role = modelMapper.map(roleDto, Roles.class);
//        role.setCreatedAt(roleDto.getSubmissionDateConverted());
        return role;
    }
}
