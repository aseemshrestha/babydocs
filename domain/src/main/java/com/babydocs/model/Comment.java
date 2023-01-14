package com.babydocs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@Table(name = "comment")
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String comment;
    private String commentedBy;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;

}