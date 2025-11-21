package com.comfandi.phobos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.comfandi.phobos.entity.FosfecStatusQuery;

@Repository
public interface FosfecStatusQueryRepository extends JpaRepository<FosfecStatusQuery, Long> {

    List<FosfecStatusQuery> findByDocumentNumber(String documentNumber);

    List<FosfecStatusQuery> findByStatus(String status);
}