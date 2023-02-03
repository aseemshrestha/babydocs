package com.babydocs.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Data
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "Title is required")
    @Column(nullable = false)
    private String title;
    @Column(length = 10485760)
    private String description;
    private String albumName;
    private String ip;
    private String browser;
    private String postType;
    @Column(nullable = false)
    private String postedBy;
    private int commentCount;
    private int likeCount;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comment;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", fetch = FetchType.LAZY)
    private List<MediaFiles> mediaFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", fetch = FetchType.LAZY)
    private List<Likes> likes;
}