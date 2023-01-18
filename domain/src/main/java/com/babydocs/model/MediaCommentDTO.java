package com.babydocs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class MediaCommentDTO {
    public Long mediaId;
    @NotEmpty(message = "Comment is required")
    public String comment;
}
