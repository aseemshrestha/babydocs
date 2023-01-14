package com.babydocs.model;

import com.babydocs.Constants;
import com.babydocs.annotations.ValidEnum;
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
    private String description;
    private String albumName;
    private String ip;
    private String browser;
    @ValidEnum(targetClassType = Constants.PostType.class, message = "Post type is not valid")
    private String postType;
    @Column(nullable = false)
    private String postedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comment;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", fetch = FetchType.LAZY)
    private List<MediaFiles> mediaFiles;
}