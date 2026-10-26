package Medco.PremuimFinancing.Repository.Insurance;

import Medco.PremuimFinancing.Entity.Insurance.Insurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceRepository extends JpaRepository<Insurance,Long> {

    Page<Insurance> findByInsuranceName(String search, Pageable pageable);

    Optional <Insurance> findByInsuranceUuid(String insuranceUuid);
}
