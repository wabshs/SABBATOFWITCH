package com.taffy.neko.models.dto;


import lombok.Data;

@Data
public class UpdateUserProfileDTO {
    private String nickName;

    private String sign;

    private String avatarUrl;

    private String aboutMe;

    private String id;
}
