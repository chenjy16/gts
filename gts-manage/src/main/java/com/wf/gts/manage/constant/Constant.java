package com.wf.gts.manage.constant;

public interface Constant {

    String applicationName = "gts-manager";

    String REDIS_PRE_FIX = "transaction:group:%s";

    String REDIS_KEYS = "transaction:group:*";

    String httpCommit = "http://%s/tx/manager/httpCommit";

    String httpRollback = "http://%s/tx/manager/httpRollback";

    String TX_MANAGER_INFO ="gtsManage/tx/manager/findTxManagerInfo";
}
