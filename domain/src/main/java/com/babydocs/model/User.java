package com.babydocs.model;

import com.babydocs.annotations.EnumValidation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user", indexes = {@Index(name = "idxemail", columnList = "email", unique = true), @Index(name = "idxusername", columnList = "username", unique = true)})
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
    @Email
    private String email;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String ip;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String browser;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int isActive;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date lastUpdated;
    @EnumValidation()
    private String gender;

    @ManyToOne
    @JoinColumn
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;


}
