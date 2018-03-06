/*package com.wf.gts.nameserver.processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wf.gts.nameserver.NamesrvController;
import com.wf.gts.nameserver.header.GetRouteInfoRequestHeader;
import com.wf.gts.nameserver.remoting.exception.RemotingCommandException;
import com.wf.gts.nameserver.remoting.protocol.RemotingCommand;
import com.wf.gts.nameserver.remoting.protocol.ResponseCode;
import com.wf.gts.nameserver.remoting.protocol.TopicRouteData;

import io.netty.channel.ChannelHandlerContext;

public class ClusterTestRequestProcessor extends DefaultRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.NAMESRV_LOGGER_NAME);
    private final DefaultMQAdminExt adminExt;
    private final String productEnvName;

    public ClusterTestRequestProcessor(NamesrvController namesrvController, String productEnvName) {
        super(namesrvController);
        this.productEnvName = productEnvName;
        adminExt = new DefaultMQAdminExt();
        adminExt.setInstanceName("CLUSTER_TEST_NS_INS_" + productEnvName);
        adminExt.setUnitName(productEnvName);
        try {
            adminExt.start();
        } catch (MQClientException e) {
            log.error("Failed to start processor", e);
        }
    }

    @Override
    public RemotingCommand getRouteInfoByTopic(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
        final RemotingCommand response = RemotingCommand.createResponseCommand(null);
        final GetRouteInfoRequestHeader requestHeader =
            (GetRouteInfoRequestHeader) request.decodeCommandCustomHeader(GetRouteInfoRequestHeader.class);

        TopicRouteData topicRouteData = this.namesrvController.getRouteInfoManager().pickupTopicRouteData(requestHeader.getTopic());
        if (topicRouteData != null) {
            String orderTopicConf =
                this.namesrvController.getKvConfigManager().getKVConfig(NamesrvUtil.NAMESPACE_ORDER_TOPIC_CONFIG,
                    requestHeader.getTopic());
            topicRouteData.setOrderTopicConf(orderTopicConf);
        } else {
            try {
                topicRouteData = adminExt.examineTopicRouteInfo(requestHeader.getTopic());
            } catch (Exception e) {
                log.info("get route info by topic from product environment failed. envName={},", productEnvName);
            }
        }

        if (topicRouteData != null) {
            byte[] content = topicRouteData.encode();
            response.setBody(content);
            response.setCode(ResponseCode.SUCCESS);
            response.setRemark(null);
            return response;
        }

        response.setCode(ResponseCode.TOPIC_NOT_EXIST);
        response.setRemark("No topic route info in name server for the topic: " + requestHeader.getTopic()
            + FAQUrl.suggestTodo(FAQUrl.APPLY_TOPIC_URL));
        return response;
    }
}
*/