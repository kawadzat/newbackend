package io.getarrays.securecapita.itinventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.getarrays.securecapita.dto.UserDTO;
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

    private String manufacturer;

    private String model;

    private Long manufacturerModel; // ID of the laptop model to copy manufacturer/model from
    
    // Custom setter to handle manufacturerModel as string or number
    @com.fasterxml.jackson.annotation.JsonSetter("manufacturerModel")
    public void setManufacturerModelFromJson(Object value) {
        if (value == null) {
            this.manufacturerModel = null;
        } else if (value instanceof String) {
            try {
                this.manufacturerModel = Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                this.manufacturerModel = null;
            }
        } else if (value instanceof Number) {
            this.manufacturerModel = ((Number) value).longValue();
        }
    }

    private Integer ram;

    private Integer processor;

    private String status;

    @com.fasterxml.jackson.annotation.JsonProperty("assertType")
    private String AssetType;

    private String issuedTo;

    private String email;

    private String department;

    private String station;

    private String designation;

    private Date purchaseDate; // For purchaseDate from frontend

    private UserDTO issuedByUser;

    private Set<UserDTO> users;

}
