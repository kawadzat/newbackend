package io.getarrays.securecapita.laptopmoverequests;

import io.getarrays.securecapita.assertmoverequests.AssertMoveStatus;
import io.getarrays.securecapita.asserts.model.Station;
import io.getarrays.securecapita.domain.User;
import io.getarrays.securecapita.itinventory.Laptop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "laptop_move_requests")
@NamedEntityGraph(name = "laptop-move-requests-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("station"),
                @NamedAttributeNode("laptop"),
                @NamedAttributeNode("initiatedBy"),
                @NamedAttributeNode("approvedBy")
        }
)
public class LaptopMoveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Station station;
    
    private String reason;
    
    @ManyToOne
    private Laptop laptop;
    
    @ManyToOne
    private User initiatedBy;
    
    @ManyToOne
    private User approvedBy;
    
    private Timestamp createdDate;
    private Timestamp updatedDate;
    
    @Enumerated(EnumType.STRING)
    private AssertMoveStatus status;
}


