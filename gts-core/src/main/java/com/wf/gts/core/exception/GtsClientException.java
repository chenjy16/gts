package com.wf.gts.core.exception;



public class GtsClientException extends Exception {
    private static final long serialVersionUID = -5758410930844185841L;
    private int responseCode;
    private String errorMessage;

    public GtsClientException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.responseCode = -1;
        this.errorMessage = errorMessage;
    }

    public GtsClientException(int responseCode, String errorMessage) {
        super(errorMessage);
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public GtsClientException setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
