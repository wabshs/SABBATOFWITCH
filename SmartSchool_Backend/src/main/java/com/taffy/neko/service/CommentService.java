package com.taffy.neko.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taffy.neko.Result.R;
import com.taffy.neko.entity.Comment;
import com.taffy.neko.models.dto.NoticeUserDTO;


public interface CommentService extends IService<Comment> {


    R<?> commentList(String articleId, Integer pageNum, Integer pageSize);

    R<?> addComment(Comment comment);

    R<?> noticeUser(NoticeUserDTO reqDTO);
}
