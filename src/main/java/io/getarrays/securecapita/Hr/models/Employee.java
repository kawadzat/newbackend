package io.getarrays.securecapita.Hr.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.getarrays.securecapita.Hr.Person;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Employee extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="employeetypeid", insertable=false, updatable=false)
    private EmployeeType employeeType;
    private Integer employeetypeid;
    private String photo;
    private String username;

    @ManyToOne
    @JoinColumn(name="jobtitleid", insertable=false, updatable=false)
    private JobTitle jobTitle;
    private Integer jobtitleid;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date hireDate;
}
