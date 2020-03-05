package com.github.repository;

import com.github.entity.CustomQueryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomQueryLogRepository extends JpaRepository<CustomQueryLog, Long>, JpaSpecificationExecutor {

    List<CustomQueryLog> findBySqlIdOrderByCreateTimeDesc(Long sqlId);
}
