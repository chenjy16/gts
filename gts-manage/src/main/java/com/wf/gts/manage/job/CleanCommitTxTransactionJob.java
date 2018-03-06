package com.wf.gts.manage.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.wf.gts.manage.service.TxManagerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 处理已经提交了的事物
 */
public class CleanCommitTxTransactionJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(CleanCommitTxTransactionJob.class);

    /**
     *
     */
    @Autowired
    private TxManagerService txManagerService;

    @Override
    public void execute(ShardingContext shardingContext) {
    	txManagerService.removeAllCommit();
    }
}
