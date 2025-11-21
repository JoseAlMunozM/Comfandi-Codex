package com.comfandi.korlon.repositories;

import com.comfandi.korlon.entities.CountableDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountableDataRepository extends JpaRepository<CountableDataEntity, Long> {

    @Query(value = "SELECT c FROM CountableDataEntity c WHERE c.regional=:regional " +
            "AND c.profile=:accountType AND c.group=:group AND c.internalGroup=:internalGroup")
    public CountableDataEntity getCountableDataByParameters(String regional,String accountType,int group, int internalGroup);
}
