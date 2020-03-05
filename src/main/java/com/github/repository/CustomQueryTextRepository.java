package com.github.repository;

import com.github.entity.CustomQueryText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomQueryTextRepository extends JpaRepository<CustomQueryText, Long>, JpaSpecificationExecutor {

    void deleteBySqlIdAndTextTypeIn(Long sqlId, String[] textTypes);

    CustomQueryText findFirstBySqlIdAndTextType(Long sqlId, String textType);
}
