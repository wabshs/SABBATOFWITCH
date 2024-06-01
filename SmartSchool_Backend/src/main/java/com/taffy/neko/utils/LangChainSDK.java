package com.taffy.neko.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.taffy.neko.Exception.ServiceException;
import com.taffy.neko.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class LangChainSDK {

    private final WebClient webClient;

    public LangChainSDK() {
        // 初始化 WebClient 配置
        this.webClient = WebClient.builder()
                .baseUrl("http://127.0.0.1:7861") // 设置基础URL
                .build();
    }

    public Mono<String> chatWithAI(String content) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("query", content); //问题内容
            body.put("knowledge_base_name", "hbut"); //模型名字
            body.put("top_k", 3);
            body.put("score_threshold", 2); //0-2 模型匹配值
            body.put("stream", false);
            body.put("model_name", "qwen-api"); //模型名字
            body.put("temperature", 0.7); //默认
            body.put("max_tokens", 0);
            body.put("prompt_name", "default");

            return webClient.post()
                    .uri("/chat/knowledge_base_chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        //现在返回值的类型是String类型的JSON----data:{answer:"xxx"}
                        String jsonStr = response.replace("data:", "");
                        //转成真JSON
                        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
                        //只要answer
                        return jsonObject.getStr("answer");
                    })
                    .doOnError(error -> {
                        log.error("LangChain Chat Error: {}", error.getMessage());
                    });
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            throw new ServiceException(ResponseEnum.LANG_CHAIN_CHAT_ERROR);
        }
    }

    /**
     * 获取知识库文件列表
     *
     * @return List[]
     */
    public Mono<List<String>> getKnowledgeList() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/knowledge_base/list_files")
                        .queryParam("knowledge_base_name", "hbut")
                        .build())
                .header("accept", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    JSONObject jsonObject = JSONUtil.parseObj(response);
                    return jsonObject.getJSONArray("data").toList(String.class);
                });
        //返回值data的数组

    }

    /**
     * 下载知识库文件
     *
     * @param fileName 文件名
     * @return file
     */
    public Mono<ResponseEntity<InputStreamResource>> downloadFile(String fileName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/knowledge_base/download_doc")
                        .queryParam("knowledge_base_name", "hbut")
                        .queryParam("file_name", fileName)
                        .build()
                )
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(byte[].class)
                .map(bytes -> {
                    try {
                        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(bytes));
                        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .contentLength(bytes.length)
                                .body(resource);
                    } catch (UnsupportedEncodingException e) {
                        // 处理编码异常
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                });
    }


    /**
     * 上传知识库文件
     *
     * @param file 文件
     * @return Mono
     */
    public Mono<String> uploadKnowledgeFile(MultipartFile file) {
        try {
            // 1. 构建请求体
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            bodyBuilder.part("files", byteArrayResource)
                    .filename(Objects.requireNonNull(file.getOriginalFilename()));
            bodyBuilder.part("knowledge_base_name", "hbut");
            // 2. 发送请求
            return webClient
                    .post()
                    .uri("/knowledge_base/upload_docs")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    // 3. 发送请求并接收响应
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    public Mono<String> deleteKnowledgeDoc(String fileName) {
        String url = "/knowledge_base/delete_docs";
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("knowledge_base_name", "hbut");
        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileName);
        requestBody.put("file_names", fileNames);
        requestBody.put("delete_content", true);
        requestBody.put("not_refresh_vs_cache", false);
        // 发送请求
        return webClient.post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class);
    }

}