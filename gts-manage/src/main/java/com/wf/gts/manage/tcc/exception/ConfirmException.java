package com.wf.gts.manage.tcc.exception;
import com.wf.gts.manage.entity.TccErrorResponse;

public class ConfirmException extends RuntimeException {

    private static final long serialVersionUID = 3665563233664481931L;

    private TccErrorResponse errorResponse;

    public ConfirmException(String message) {
        super(message);
    }

    public ConfirmException(String message, TccErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }

    public ConfirmException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfirmException(Throwable cause) {
        super(cause);
    }

    protected ConfirmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TccErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
