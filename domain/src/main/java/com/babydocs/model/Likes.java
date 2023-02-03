package com.babydocs.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data
@Table(name = "likes")
public class Likes implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String likedBy;
    @JsonIgnore
    @ManyToOne
    private Post post;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "likedOn", nullable = false, updatable = false)
    @CreatedDate
    private Date likedOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;

}