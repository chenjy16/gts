package com.wf.gts.common.serizlize.protostuff;
import com.wf.gts.common.serizlize.MessageCodecService;
import com.wf.gts.common.serizlize.MessageDecoder;

/**
 *  Protostuff 转换器
 */
public class ProtostuffDecoder extends MessageDecoder {

    public ProtostuffDecoder(MessageCodecService util) {
        super(util);
    }
}

