package io.getarrays.securecapita.Lines;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`lines`")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lines {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "network")
    private String network;
    
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public enum Status {
        ACTIVE, INACTIVE
    }
}