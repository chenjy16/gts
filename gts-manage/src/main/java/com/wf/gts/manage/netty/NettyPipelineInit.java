package com.wf.gts.manage.netty;
import com.wf.gts.common.enums.SerializeProtocolEnum;
import com.wf.gts.common.serizlize.kryo.KryoCodecService;
import com.wf.gts.common.serizlize.kryo.KryoDecoder;
import com.wf.gts.common.serizlize.kryo.KryoEncoder;
import com.wf.gts.common.serizlize.kryo.KryoPoolFactory;
import com.wf.gts.common.serizlize.protostuff.ProtostuffCodecService;
import com.wf.gts.common.serizlize.protostuff.ProtostuffDecoder;
import com.wf.gts.common.serizlize.protostuff.ProtostuffEncoder;

import io.netty.channel.ChannelPipeline;


public class NettyPipelineInit {
    public static void serializePipeline(SerializeProtocolEnum serializeProtocol, ChannelPipeline pipeline) {
        switch (serializeProtocol) {
            case PROTOSTUFF:
                ProtostuffCodecService protostuffCodecService = new ProtostuffCodecService();
                pipeline.addLast(new ProtostuffEncoder(protostuffCodecService));
                pipeline.addLast(new ProtostuffDecoder(protostuffCodecService));
                break;
            default:
                KryoCodecService defaultCodec = new KryoCodecService(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(defaultCodec));
                pipeline.addLast(new KryoDecoder(defaultCodec));
                break;
        }
    }
}
