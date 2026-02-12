package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private String id;
    private String avatar;
    private String background;
    private String name;
    private List<EmailEntry> emails;
    private List<PhoneEntry> phoneNumbers;
    private String title;
    private String company;
    private String birthday;
    private String address;
    private String notes;
    private List<String> tags;
    private List<String> roles;

    /**
     * Only populated on user creation. Contains the temporary password
     * that the user must change on first login.
     */
    private String temporaryPassword;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailEntry {
        private String email;
        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneEntry {
        private String country;
        private String phoneNumber;
        private String label;
    }
}
