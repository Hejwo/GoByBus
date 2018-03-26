package org.hejwo.gobybus.commons.integration.warsawapi;

import com.google.common.collect.Maps;
import feign.Request;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class WarsawApiKeyClearingLoggerTest {

    private WarsawApiKeyClearingLogger logger = new WarsawApiKeyClearingLogger();

    @Test
    public void createWithRemovedApiKey_shouldReturnEmpty_whenEmpty() {
        String url = "";
        Request request = Request.create("GET", url, Maps.newHashMap(), "body".getBytes(), Charset.defaultCharset());

        Request withRemovedApiKey = logger.createWithRemovedApiKey(request);

        assertThat(withRemovedApiKey.url()).isEqualToIgnoringCase("");
    }

    @Test
    public void createWithRemovedApiKey_shouldReturnHiddenApiKeyOnly_whenProperKey() {
        String url = "http://localhost:1111/api/action/wsstore_get/?id=c7238cfe-8b1f-4c38-bb4a-de386db7e776&apikey=qwertya1-333f-cde2-abc1-cfa1826a2d0f";
        Request request = Request.create("GET", url, Maps.newHashMap(), "body".getBytes(), Charset.defaultCharset());

        Request withRemovedApiKey = logger.createWithRemovedApiKey(request);

        assertThat(withRemovedApiKey.url()).isEqualToIgnoringCase("http://localhost:1111/api/action/wsstore_get/?id=c7238cfe-8b1f-4c38-bb4a-de386db7e776&apikey=????????-333f-cde2-abc1-cfa1826a2d0f");
    }

}