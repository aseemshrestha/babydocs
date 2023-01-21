package com.babydocs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class SwitchPostVisibilityDTO {
    @NotNull
    public Long postId;
    @NotNull
    public String postType;
}
