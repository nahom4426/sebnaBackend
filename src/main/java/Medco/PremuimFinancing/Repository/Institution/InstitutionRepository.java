package Medco.PremuimFinancing.Repository.Institution;

import Medco.PremuimFinancing.Entity.Institution.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
    Page<Institution> findAll(Pageable pageable);
}
