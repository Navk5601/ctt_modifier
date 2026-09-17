package com.example.ctt_converter_api.service;

import com.example.ctt_converter_api.model.JarExecutionResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

@Service
public class JarExecutionService {

    private static final String JAVA_COMMAND = "java";

    private static final String JAR_PATH =
            "/home/naveen/GitHub/TreSureX/ctt-convertor/target/" +
                    "CTT-ZipModifier-1.0.18-SNAPSHOT.jar";

    private static final String OUTPUT_PATH =
            "/home/naveen/Projects/";

    public JarExecutionResponse execute(
            MultipartFile inputFile,
            MultipartFile configFile,
            boolean ignoreCase) {

        Path tempInputFile = null;
        Path tempConfigFile = null;

        try {

            validateFiles(inputFile, configFile);

            // -----------------------------------------
            // Create temporary files
            // -----------------------------------------

            tempInputFile = Files.createTempFile(
                    "ctt_input_",
                    getFileExtension(inputFile.getOriginalFilename())
            );

            tempConfigFile = Files.createTempFile(
                    "ctt_config_",
                    getFileExtension(configFile.getOriginalFilename())
            );

            inputFile.transferTo(tempInputFile);
            configFile.transferTo(tempConfigFile);


            // -----------------------------------------
            // Build command
            // -----------------------------------------

            List<String> commands = buildCommand(
                    tempConfigFile, tempInputFile, ignoreCase);


            // -----------------------------------------
            // Execute JAR
            // -----------------------------------------

            ProcessBuilder processBuilder =
                    new ProcessBuilder(commands);

            processBuilder.redirectErrorStream(true);

            Process process =
                    processBuilder.start();


            // -----------------------------------------
            // Capture JAR output
            // -----------------------------------------

            String output;

            try (InputStream inputStream =
                         process.getInputStream()) {

                output = new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }


            // -----------------------------------------
            // Wait for process
            // -----------------------------------------

            int exitCode =
                    process.waitFor();


            // -----------------------------------------
            // Build response
            // -----------------------------------------

            JarExecutionResponse response =
                    new JarExecutionResponse();

            response.setExitCode(exitCode);
            response.setSuccess(exitCode == 0);
            response.setOutput(output);

            return response;


        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return JarExecutionResponse.failure(
                    "JAR execution was interrupted.",
                    e
            );

        } catch (Exception e) {

            return JarExecutionResponse.failure(
                    "Failed to execute JAR.",
                    e
            );

        } finally {

            // -----------------------------------------
            // Cleanup temporary files
            // -----------------------------------------

            deleteFile(tempInputFile);
            deleteFile(tempConfigFile);
        }
    }


    private List<String> buildCommand(Path tempConfigFile,
                                      Path tempInputFile,
                                      boolean ignoreCase) {

        List<String> commands = new ArrayList<>();

        commands.add(JAVA_COMMAND);
        commands.add("-jar");
        commands.add(JAR_PATH);

        commands.add("-cfp");
        commands.add(tempConfigFile.toAbsolutePath().toString());

        commands.add("-zfp");
        commands.add(tempInputFile.toAbsolutePath().toString());

        commands.add("-ofp");
        commands.add(OUTPUT_PATH);

        commands.add("-icm");
        commands.add(String.valueOf(ignoreCase));

        return commands;
    }

    private void validateFiles(
            MultipartFile inputFile,
            MultipartFile configFile) {

        if (inputFile == null ||
                inputFile.isEmpty()) {

            throw new IllegalArgumentException(
                    "Input file is required."
            );
        }

        if (configFile == null ||
                configFile.isEmpty()) {

            throw new IllegalArgumentException(
                    "Configuration file is required."
            );
        }
    }


    private String getFileExtension(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return ".tmp";
        }

        int index = fileName.lastIndexOf('.');

        if (index == -1) {
            return ".tmp";
        }

        return fileName.substring(index);
    }


    private void deleteFile(Path path) {

        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {

            // Log instead of throwing from finally
            System.err.println(
                    "Unable to delete temporary file: " + path
            );
        }
    }

}
