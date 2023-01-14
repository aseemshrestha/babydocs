package com.babydocs.model;

import com.babydocs.annotations.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "baby")
@Data
public class Baby implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "First Name is required")
    @Column(nullable = false)
    private String firstName;

    @NotEmpty(message = "Last Name is required")
    @Column(nullable = false)
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob", nullable = false)
    @NotEmpty(message = "Dob is required")
    private Date dob;

    @ValidEnum(targetClassType = Gender.class, message = "Gender is empty or not valid")
    private String gender;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_username", referencedColumnName = "username", nullable = false)
    private User user;


}