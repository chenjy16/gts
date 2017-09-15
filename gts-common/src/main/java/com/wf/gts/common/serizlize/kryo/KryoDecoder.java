package com.wf.gts.common.serizlize.kryo;
import com.wf.gts.common.serizlize.MessageCodecService;
import com.wf.gts.common.serizlize.MessageDecoder;

/**
 *  Kryo 转换器
 */
public class KryoDecoder  extends MessageDecoder {

    public KryoDecoder(MessageCodecService service) {
        super(service);
    }
}
