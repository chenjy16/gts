package com.wf.gts.nameserver.kvconfig;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wf.gts.nameserver.NameServerInstanceTest;

public class KVConfigManagerTest extends NameServerInstanceTest {
    private KVConfigManager kvConfigManager;
    
    public static final String NAMESPACE_ORDER_TOPIC_CONFIG = "ORDER_TOPIC_CONFIG";

    @Before
    public void setup() throws Exception {
        kvConfigManager = new KVConfigManager(nameSrvController);
    }

    @Test
    public void testPutKVConfig() {
        kvConfigManager.putKVConfig(NAMESPACE_ORDER_TOPIC_CONFIG, "UnitTest", "test");
        byte[] kvConfig = kvConfigManager.getKVListByNamespace(NAMESPACE_ORDER_TOPIC_CONFIG);
        assertThat(kvConfig).isNotNull();
        String value = kvConfigManager.getKVConfig(NAMESPACE_ORDER_TOPIC_CONFIG, "UnitTest");
        assertThat(value).isEqualTo("test");
    }

    @Test
    public void testDeleteKVConfig() {
        kvConfigManager.deleteKVConfig(NAMESPACE_ORDER_TOPIC_CONFIG, "UnitTest");
        byte[] kvConfig = kvConfigManager.getKVListByNamespace(NAMESPACE_ORDER_TOPIC_CONFIG);
        assertThat(kvConfig).isNull();
        Assert.assertTrue(kvConfig == null);
        String value = kvConfigManager.getKVConfig(NAMESPACE_ORDER_TOPIC_CONFIG, "UnitTest");
        assertThat(value).isNull();
    }
}