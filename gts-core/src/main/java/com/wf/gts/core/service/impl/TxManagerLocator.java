package com.wf.gts.core.service.impl;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.wf.gts.common.entity.TxManagerServer;
import com.wf.gts.common.entity.TxManagerServiceDTO;
import com.wf.gts.common.utils.OkHttpTools;
import com.wf.gts.core.concurrent.TxTransactionThreadFactory;
import com.wf.gts.core.config.TxConfig;
import com.wf.gts.core.constant.Constant;

public class TxManagerLocator {

    private static final TxManagerLocator TX_MANAGER_LOCATOR = new TxManagerLocator();
    
    public static TxManagerLocator getInstance() {
        return TX_MANAGER_LOCATOR;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerLocator.class);
    private TxConfig txConfig;
    private ScheduledExecutorService m_executorService;
    private AtomicReference<List<TxManagerServiceDTO>> m_configServices;
    private Type m_responseType;

    public void setTxConfig(TxConfig txConfig) {
        this.txConfig = txConfig;
    }

    private TxManagerLocator() {
        List<TxManagerServiceDTO> initial = Lists.newArrayList();
        m_configServices = new AtomicReference<>(initial);
        m_responseType = new TypeToken<List<TxManagerServiceDTO>>() {}.getType();
        this.m_executorService = Executors.newSingleThreadScheduledExecutor(
                TxTransactionThreadFactory.create("TxManagerLocator", true));
    }

    /**
     * 获取TxManager 服务信息
     * @return TxManagerServer
     */
    public TxManagerServer locator() {
        int maxRetries = 2;
        final List<TxManagerServiceDTO> txManagerService = getTxManagerService();
        if (CollectionUtils.isEmpty(txManagerService)) {
            return null;
        }
        for (int i = 0; i < maxRetries; i++) {
            List<TxManagerServiceDTO> randomServices = Lists.newLinkedList(txManagerService);
            Collections.shuffle(randomServices);
            for (TxManagerServiceDTO serviceDTO : randomServices) {
                String url = String.join("", serviceDTO.getHomepageUrl(), "gtsManage/tx/manager", Constant.FIND_SERVER);
                LOGGER.debug("Loading service from {}", url);
                try {
                  TxManagerServer server=OkHttpTools.getInstance().get(url, null, TxManagerServer.class);
                  return server;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    LOGGER.error("loadTxManagerServer fail exception:{}", ex.getMessage());

                }
            }
        }
        return null;
    }


    private List<TxManagerServiceDTO> getTxManagerService() {
        if (m_configServices.get().isEmpty()) {
            updateTxManagerServices();
        }
        return m_configServices.get();
    }

    /**
     * 功能描述: 定时刷新事务管理器服务地址
     * @author: chenjy
     * @date: 2017年9月18日 下午3:51:03
     */
    public void schedulePeriodicRefresh() {
        this.m_executorService.scheduleAtFixedRate(
                () -> {
                    LOGGER.info("refresh updateTxManagerServices delayTime:{}",txConfig.getRefreshInterval());
                    updateTxManagerServices();
                }, 0, txConfig.getRefreshInterval(),
                TimeUnit.SECONDS);
    }

    
    /**
     * 功能描述: 刷新事务管理器服务地址
     * @author: chenjy
     * @date: 2017年9月18日 下午3:50:11
     */
    private synchronized void updateTxManagerServices() {
        String url = assembleUrl();
        int maxRetries = 2;
        for (int i = 0; i < maxRetries; i++) {
            try {
                final List<TxManagerServiceDTO> serviceDTOList =
                        OkHttpTools.getInstance().get(url, m_responseType);
                
                if (CollectionUtils.isEmpty(serviceDTOList)) {
                    LOGGER.error("Empty response! 请求url为:{}",url);
                    continue;
                }
                m_configServices.set(serviceDTOList);
                return;
            } catch (Throwable ex) {
                ex.printStackTrace();
                LOGGER.error("updateTxManagerServices fail exception:{}", ex.getMessage());
            }
        }
    }

    private String assembleUrl() {
        return String.join("", txConfig.getTxManagerUrl(), Constant.TX_MANAGER_PRE, Constant.LOAD_TX_MANAGER_SERVICE_URL);
    }

}
