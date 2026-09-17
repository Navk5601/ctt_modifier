package com.example.ctt_converter_api.controller;

import com.example.ctt_converter_api.model.JarExecutionResponse;
import com.example.ctt_converter_api.service.JarExecutionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class JarExecutionController {

    private final JarExecutionService jarExecutionService;

    public JarExecutionController(JarExecutionService jarExecutionService) {
        this.jarExecutionService = jarExecutionService;
    }

    @PostMapping(
            value = "/jar/execute",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JarExecutionResponse> execute(
            @RequestPart("inputFile") MultipartFile inputFile,
            @RequestPart("configFile") MultipartFile configFile,
            @RequestParam(defaultValue = "false") boolean ignoreCase) {

        JarExecutionResponse response =
                jarExecutionService.execute(
                        inputFile,
                        configFile,
                        ignoreCase
                );

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
