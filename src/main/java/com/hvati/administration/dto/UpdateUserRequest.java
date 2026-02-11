package com.hvati.administration.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String id;
    private UserDto contact;
}
