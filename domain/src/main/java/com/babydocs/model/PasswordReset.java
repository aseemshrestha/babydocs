package com.babydocs.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
public class PasswordReset
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    @NotNull
    private String username;
    @NotNull
    private String resetCode;
    @Temporal( TemporalType.TIMESTAMP )
    private Date expiresAt;
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "created", nullable = false, updatable = false )
    @CreatedDate
    private Date created;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "lastUpdated", nullable = false )
    @LastModifiedDate
    private Date lastUpdated;
}
