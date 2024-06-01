package com.taffy.neko.controller;


import com.taffy.neko.Result.R;
import com.taffy.neko.enums.ResponseEnum;
import com.taffy.neko.service.AIService;
import com.taffy.neko.utils.LangChainSDK;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ai")
@Api(tags = "AI助手模块")
public class AIController {

    @Resource
    private AIService aiService;

    @Resource
    private LangChainSDK langChainSDK;

    @PostMapping("/chat")
    @ApiOperation(value = "和本地知识库对话")
    public R<?> chatWithAI(@RequestParam String content) {
        return new R<>().success(ResponseEnum.SUCCESS, aiService.chatWithAI(content));
    }

    @GetMapping("/getKnowledgeList")
    @ApiOperation(value = "获取知识库里的文件列表")
    public R<?> getKnowledgeBasesList() {
        return aiService.getKnowledgeList();
    }

    @GetMapping("/downloadFile")
    @ApiOperation(value = "下载知识库文件")
    public Mono<ResponseEntity<InputStreamResource>> downloadKnowledgeFile(@RequestParam String fileName) {
        return langChainSDK.downloadFile(fileName);
    }

    @PostMapping("/uploadFile")
    @ApiOperation(value = "上传知识库文件")
    public Mono<String> uploadFile(@RequestParam("files") MultipartFile file) {
        return langChainSDK.uploadKnowledgeFile(file);
    }

    @PostMapping("/deleteKnowledgeDoc")
    @ApiOperation(value = "删除知识库")
    public Mono<String> deleteKnowledgeDoc(@RequestParam String fileName) {
        return langChainSDK.deleteKnowledgeDoc(fileName);
    }

}
