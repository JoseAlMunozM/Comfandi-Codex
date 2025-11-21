package com.comfandi.phobos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.entity.ValidationResult;
import com.comfandi.phobos.repository.ValidationResultRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ValidationResultService {

    @Autowired
    private ValidationResultRepository repository;

    public long countDisabledUsers(LocalDate cutoffDate) {
        return repository.countDisabledUsers(cutoffDate);
    }

    public Page<ValidationResult> getDisabledUsers(LocalDate cutoffDate, Pageable pageable) {
        return repository.findDisabledUsers(cutoffDate, pageable);
    }

    public List<ValidationResult> getAllDisabledUsers(LocalDate cutoffDate) {
        return repository.findAllDisabledUsers(cutoffDate);
    }
}