package com.babydocs.model;


import com.babydocs.annotations.ValidEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user", indexes = {@Index(name = "idxemail", columnList = "email", unique = true), @Index(name = "idxusername", columnList = "username", unique = true)})
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "First Name is required")
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(message = "Last Name is required")
    private String lastName;

    @Column(nullable = false, unique = true, length = 200)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(message = "Password is required")
    private String password;

    @Column(nullable = false, unique = true, length = 200)
    @NotEmpty(message = "Email is required")
    private String email;

    private String ip;
    private String browser;
    private int isActive;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;

    @ValidEnum(targetClassType = Gender.class, message = "Gender is empty or not valid")
    private Gender gender;
    @ManyToOne
    @JoinColumn
    private Role role;


}
