package com.github.repository;

import com.github.entity.CustomQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomQueryRepository extends JpaRepository<CustomQuery, Long>, JpaSpecificationExecutor {
}
