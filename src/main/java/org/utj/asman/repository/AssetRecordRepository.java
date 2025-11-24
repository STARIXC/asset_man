package org.utj.asman.repository;

import org.utj.asman.model.AssetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface AssetRecordRepository extends JpaRepository<AssetRecord, Long> {
    // We might need to find an asset record by its serial number later
    boolean existsByCpuSerial(String cpuSerial);
    boolean existsByMonitorSerial(String monitorSerial);
    boolean existsByUpsSerial(String upsSerial);
    Optional<AssetRecord> findByCpuSerial(String cpuSerial);
    Optional<AssetRecord> findByMonitorSerial(String monitorSerial);
    Optional<AssetRecord> findByUpsSerial(String upsSerial);
    List<AssetRecord> findByFacilityId(Long facilityId);

    @Query("SELECT DISTINCT a.cpuModel FROM AssetRecord a WHERE a.cpuModel LIKE :query%")
    List<String> searchCpuModels(@Param("query") String query);

    @Query("SELECT DISTINCT a.monitorModel FROM AssetRecord a WHERE a.monitorModel LIKE :query%")
    List<String> searchMonitorModels(@Param("query") String query);

    @Query("SELECT DISTINCT a.upsModel FROM AssetRecord a WHERE a.upsModel LIKE :query%")
    List<String> searchUpsModels(@Param("query") String query);
}
