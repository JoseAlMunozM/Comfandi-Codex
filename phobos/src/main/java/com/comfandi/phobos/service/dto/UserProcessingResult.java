package com.comfandi.phobos.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserProcessingResult {
    private List<UserDto> usersToValidate;
    private List<UserDto> existingUsers;
}