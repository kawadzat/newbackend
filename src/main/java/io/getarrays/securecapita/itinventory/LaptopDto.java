package io.getarrays.securecapita.itinventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.getarrays.securecapita.dto.UserDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LaptopDto {

    private Long id;

    private String title;

    private String description;

    private Date issueDate;

    private Date replacementDate;

    private String serialNumber;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    private String model; // Optional field

    private Integer ram;

    private Integer processor;

    private String status;

    private String issuedTo;

    private String email;

    private String department;

    private String designation;

    private UserDTO issuedByUser;

    private Set<UserDTO> users;

}
