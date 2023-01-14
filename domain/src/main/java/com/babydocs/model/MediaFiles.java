package com.babydocs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "media")
public class MediaFiles implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String mediaType;
    private String mediaLocation;
    private String mediaDescription;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;


}