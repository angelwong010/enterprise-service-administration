package com.hvati.administration.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private String id;
    private String name;
    private String description;
    private int usersCount;
    private boolean isActive;
    private String updatedAt;
    private List<String> permissions;
}
