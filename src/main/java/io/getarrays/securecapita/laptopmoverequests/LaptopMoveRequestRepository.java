package io.getarrays.securecapita.laptopmoverequests;

import io.getarrays.securecapita.assertmoverequests.AssertMoveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaptopMoveRequestRepository extends JpaRepository<LaptopMoveRequest, Long> {
    
    @Query("SELECT DISTINCT lmr FROM LaptopMoveRequest lmr " +
            "JOIN lmr.laptop l " +
            "WHERE lmr.status = :status AND lmr.laptop.id = :laptopId")
    @EntityGraph(value = "laptop-move-requests-entity-graph")
    Optional<LaptopMoveRequest> findByLaptopIdAndStatus(@Param("laptopId") Long laptopId, @Param("status") AssertMoveStatus status);

    @Query("SELECT DISTINCT lmr FROM LaptopMoveRequest lmr WHERE lmr.id = :requestId")
    @EntityGraph(value = "laptop-move-requests-entity-graph")
    Optional<LaptopMoveRequest> findMoveRequest(@Param("requestId") Long requestId);

    @Query("SELECT lmr FROM LaptopMoveRequest lmr WHERE lmr.station.station_id = :stationId")
    @EntityGraph(value = "laptop-move-requests-entity-graph")
    Page<LaptopMoveRequest> findAllWithStationId(@Param("stationId") long stationId, PageRequest pageRequest);

    @Query("SELECT DISTINCT lmr FROM LaptopMoveRequest lmr " +
            "JOIN lmr.station s " +
            "JOIN UserStation us ON us.station = s " +
            "WHERE us.user.id = :userId")
    @EntityGraph(value = "laptop-move-requests-entity-graph")
    Page<LaptopMoveRequest> findByUserIdAndAssignedStations(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT DISTINCT lmr FROM LaptopMoveRequest lmr " +
            "JOIN lmr.station s " +
            "JOIN UserStation us ON us.station = s " +
            "WHERE us.user.id = :userId AND s.station_id = :stationId")
    @EntityGraph(value = "laptop-move-requests-entity-graph")
    Page<LaptopMoveRequest> findByUserIdAndAssignedStationsId(
            @Param("stationId") long stationId,
            @Param("userId") Long userId,
            PageRequest pageRequest
    );
}


