package com.comfandi.phobos.mapper;


import com.comfandi.phobos.entity.WorkshopEntity;
import com.comfandi.phobos.entity.WorkshopUserEntity;

import com.comfandi.phobos.service.dto.WorkshopUserDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

public class WorkshopMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static WorkshopUserEntity userDtoToWorkshopEntity(WorkshopUserDto user){
        WorkshopUserEntity entity= new WorkshopUserEntity();
        entity.setFullName(user.getFullName());
        entity.setDescription(user.getDescription());
        entity.setEmail(user.getEmail());
        entity.setStatus(user.getState());
        entity.setIdentificationNumber(user.getIdentification());
        entity.setIdentificationType(user.getIdentificationType());
        entity.setValue(user.getCourseFee().doubleValue());
        entity.setStartDate(LocalDate.parse(user.getStartDate(),formatter));
        entity.setCity(user.getCity());
        entity.setArea(user.getProgram());
        entity.setProgress(Double.parseDouble(user.getProgress()));
        entity.setOrientationDate(user.getOrientationDate()==null?null:LocalDate.parse(user.getOrientationDate(),formatter));
        entity.setValidationDate(LocalDate.now());
        entity.setYear(user.getYear());//todo: validar si viene del servicioentity.setAppointment(true);
        return entity;
    }

    public static WorkshopUserDto workoshopEntityToWorkshopUserDto(WorkshopUserEntity user){
       return WorkshopUserDto.builder().userId(user.getId())
                .city(user.getCity())
               .email(user.getEmail())
               .mobile(user.getPhone())
               .fullName(user.getFullName())
               .modalidad(user.getModality().getName())
               .regional(user.getRegional().getName())
               .charge(user.getStatus())
               .state(user.getStatus())
               .courseFee(BigDecimal.valueOf(user.getValue()))
               .program(user.getWorkshop().getName())
               .description(user.getDescription())
               .progress(user.getProgress().toString())
               .year(user.getYear())
               .identificationType(user.getIdentificationType())
               .identification(user.getIdentificationNumber())
               .orientationDate(user.getOrientationDate()!=null?user.getOrientationDate().format(formatter):"")
               .startDate((user.getStartDate().format(formatter))).build();
    }
}
