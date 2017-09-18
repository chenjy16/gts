package com.wf.gts.core.util;

public class CompensationLocal {

    private static final CompensationLocal COMPENSATION_LOCAL = new CompensationLocal();

    private CompensationLocal() {

    }

    public static CompensationLocal getInstance() {
        return COMPENSATION_LOCAL;
    }


    private static final ThreadLocal<String> currentLocal = new ThreadLocal<>();


    public void setCompensationId(String compensationId) {
        currentLocal.set(compensationId);
    }

    public String getCompensationId() {
        return currentLocal.get();
    }

    public void removeCompensationId() {
        currentLocal.remove();
    }

}
