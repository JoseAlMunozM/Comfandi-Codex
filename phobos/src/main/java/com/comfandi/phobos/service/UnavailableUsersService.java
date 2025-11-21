package com.comfandi.phobos.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.client.response.ApiUnavailableUsersResponse;
import com.comfandi.phobos.mapper.UnavailableUserMapper;
import com.comfandi.phobos.repository.UnavailableUserRepository;

@Service
public class UnavailableUsersService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    UnavailableUserRepository unavailableUserRepository;

    public List<ApiUnavailableUsersResponse> listUnavailableUsers(String startDate, String endDate){

        LocalDate start= LocalDate.parse(startDate,formatter);
        LocalDate end= LocalDate.parse(endDate,formatter);
        return unavailableUserRepository.findByDates(start,end).stream().map(UnavailableUserMapper::unavailableUserEntityToUnavailableUser).toList();
    }
}
