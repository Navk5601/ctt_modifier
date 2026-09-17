package com.example.ctt_converter_api.model;

public class JarExecutionResponse {

    private boolean success;

    private int exitCode;

    private String output;

    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static JarExecutionResponse failure(
            String message,
            Exception exception) {

        JarExecutionResponse response =
                new JarExecutionResponse();

        response.setSuccess(false);
        response.setExitCode(-1);
        response.setError(
                message + "\n" + exception.getMessage()
        );

        return response;
    }

}
