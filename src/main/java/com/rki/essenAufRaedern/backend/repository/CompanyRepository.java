package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
