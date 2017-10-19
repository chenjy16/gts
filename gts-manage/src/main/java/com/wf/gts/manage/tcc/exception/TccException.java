package com.wf.gts.manage.tcc.exception;


public class TccException extends RuntimeException {
    private static final long serialVersionUID = 2180330687914729827L;

    public TccException(String message) {
        super(message);
    }

    public TccException(String message, Throwable cause) {
        super(message, cause);
    }

    public TccException(Throwable cause) {
        super(cause);
    }

    protected TccException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
