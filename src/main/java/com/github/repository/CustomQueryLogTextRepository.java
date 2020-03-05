package com.github.repository;

import com.github.entity.CustomQueryLogText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomQueryLogTextRepository extends JpaRepository<CustomQueryLogText, Long>, JpaSpecificationExecutor {

    List<CustomQueryLogText> findByLogId(Long logId);
}
