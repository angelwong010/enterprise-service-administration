package com.hvati.administration.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDto {
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String status;
    private List<String> roles;
}
