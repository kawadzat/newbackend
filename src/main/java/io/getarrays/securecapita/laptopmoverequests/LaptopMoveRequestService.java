package io.getarrays.securecapita.laptopmoverequests;

import io.getarrays.securecapita.assertmoverequests.AssertMoveStatus;
import io.getarrays.securecapita.asserts.model.Station;
import io.getarrays.securecapita.asserts.repo.StationRepository;
import io.getarrays.securecapita.domain.User;
import io.getarrays.securecapita.dto.UserDTO;
import io.getarrays.securecapita.exception.CustomMessage;
import io.getarrays.securecapita.itinventory.Laptop;
import io.getarrays.securecapita.itinventory.LaptopRepository;
import io.getarrays.securecapita.repository.implementation.UserRepository1;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LaptopMoveRequestService {
    private final LaptopMoveRequestRepository laptopMoveRequestRepository;
    private final LaptopRepository laptopRepository;
    private final StationRepository stationRepository;
    private final UserRepository1 userRepository1;

    @Transactional
    public ResponseEntity<Object> addRequest(LaptopMoveRequestDto laptopMoveRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository1.findById(((UserDTO) authentication.getPrincipal()).getId()).get();

        Optional<Laptop> optionalLaptop = laptopRepository.findById(laptopMoveRequestDto.getLaptopId());
        if (laptopMoveRequestRepository.findByLaptopIdAndStatus(laptopMoveRequestDto.getLaptopId(), AssertMoveStatus.PENDING).isPresent()) {
            return ResponseEntity.badRequest().body(new CustomMessage("Laptop already has a pending move request."));
        }
        if (optionalLaptop.isPresent()) {
            Optional<Station> optionalStation = stationRepository.findById(laptopMoveRequestDto.getStationId());
            if (optionalStation.isPresent()) {
                LaptopMoveRequest laptopMoveRequest = LaptopMoveRequest.builder()
                        .laptop(optionalLaptop.get())
                        .station(optionalStation.get())
                        .initiatedBy(user)
                        .reason(laptopMoveRequestDto.getReason())
                        .status(AssertMoveStatus.PENDING)
                        .build();
                laptopMoveRequest.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                laptopMoveRequest.setUpdatedDate(laptopMoveRequest.getCreatedDate());
                laptopMoveRequestRepository.save(laptopMoveRequest);
                return ResponseEntity.ok(new CustomMessage("Laptop move requested to " + optionalStation.get().getStationName()));
            }
            return ResponseEntity.badRequest().body(new CustomMessage("Station not found."));
        }
        return ResponseEntity.badRequest().body(new CustomMessage("Laptop not found."));
    }

    @Transactional
    public ResponseEntity<Object> approve(Long requestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<LaptopMoveRequest> laptopMoveRequest = laptopMoveRequestRepository.findMoveRequest(requestId);
        User user = userRepository1.findById(((UserDTO) authentication.getPrincipal()).getId()).get();
        if (laptopMoveRequest.isPresent() && laptopMoveRequest.get().getStatus() == AssertMoveStatus.PENDING) {
            Optional<Laptop> optionalLaptop = laptopRepository.findById(laptopMoveRequest.get().getLaptop().getId());
            if (optionalLaptop.isPresent()) {
                Laptop laptop = optionalLaptop.get();
                laptop.setStation(laptopMoveRequest.get().getStation().getStationName());
                laptopMoveRequest.get().setStatus(AssertMoveStatus.APPROVED);
                laptopMoveRequest.get().setApprovedBy(user);
                laptopMoveRequest.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                laptopRepository.save(laptop);
                laptopMoveRequestRepository.save(laptopMoveRequest.get());
                return ResponseEntity.ok(new CustomMessage("Laptop moved to " + laptopMoveRequest.get().getStation().getStationName()));
            }
        }
        return ResponseEntity.ok(new CustomMessage("Request not found."));
    }

    public ResponseEntity<Object> reject(Long requestId) {
        Optional<LaptopMoveRequest> laptopMoveRequest = laptopMoveRequestRepository.findMoveRequest(requestId);
        if (laptopMoveRequest.isPresent()) {
            laptopMoveRequest.get().setStatus(AssertMoveStatus.REJECTED);
            laptopMoveRequest.get().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            laptopMoveRequestRepository.save(laptopMoveRequest.get());
            return ResponseEntity.ok(new CustomMessage("Laptop move request rejected."));
        }
        return ResponseEntity.ok(new CustomMessage("Request not found."));
    }
}

