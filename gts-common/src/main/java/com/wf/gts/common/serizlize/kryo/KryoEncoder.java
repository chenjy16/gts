package com.wf.gts.common.serizlize.kryo;
import com.wf.gts.common.serizlize.MessageCodecService;
import com.wf.gts.common.serizlize.MessageEncoder;

public class KryoEncoder extends MessageEncoder {

    public KryoEncoder(MessageCodecService util) {
        super(util);
    }
}
