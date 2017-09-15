package com.wf.gts.common.serizlize.protostuff;

import com.wf.gts.common.serizlize.MessageCodecService;
import com.wf.gts.common.serizlize.MessageEncoder;

/**
 *  Protostuff 转换器
 */
public class ProtostuffEncoder extends MessageEncoder {

    public ProtostuffEncoder(MessageCodecService util) {
        super(util);
    }
}

