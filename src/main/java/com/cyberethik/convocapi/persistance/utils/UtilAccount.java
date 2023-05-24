package com.cyberethik.convocapi.persistance.utils;

import com.cyberethik.convocapi.persistance.dto.AccountDto;
import com.cyberethik.convocapi.persistance.entities.Accounts;
import org.modelmapper.ModelMapper;

import java.text.ParseException;

public class UtilAccount {

    public static AccountDto convertToDto(Accounts account, ModelMapper modelMapper) {
        AccountDto accountDto = modelMapper.map(account, AccountDto.class);
//        accountDto.setSubmissionDate(account.getCreatedAt());
        return accountDto;
    }

    public static Accounts convertToEntity(AccountDto accountDto, ModelMapper modelMapper) throws ParseException {
        Accounts account = modelMapper.map(accountDto, Accounts.class);
//        account.setCreatedAt(accountDto.getSubmissionDateConverted());
        return account;
    }
}
