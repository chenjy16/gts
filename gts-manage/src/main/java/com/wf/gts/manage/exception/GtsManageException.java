package com.wf.gts.manage.exception;




public class GtsManageException extends Exception {
    private static final long serialVersionUID = 5975020272601250368L;
    private final int responseCode;
    private final String errorMessage;

    public GtsManageException(int responseCode, String errorMessage) {
        super(errorMessage);
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
