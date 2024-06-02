package com.taffy.neko.models.dto;


import lombok.Data;

@Data
public class NoticeUserDTO {


    //当前用户的id
    private String userId;

    //发送对象的id
    private String toUserId;

    //    文章标题
    private String articleId;
}
