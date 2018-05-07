package com.multicloud.deploymenttool.heroku;

import com.heroku.api.App;
import com.heroku.api.Build;
import lombok.SneakyThrows;
import org.junit.Test;

import java.net.URL;

public class HerokuDeployServiceTest {

    private HerokuDeployService herokuDeployService = new HerokuDeployService();

    @Test
    @SneakyThrows
    public void deployTest() {
//        given
        String apiKey = "c7c77a5c-f64d-472e-8963-a4ef794f3340";
//        when
        final App app = herokuDeployService.createApp(apiKey);
        final String url = "https://github.com/heroku/node-js-getting-started/archive/master.tar.gz";
        final Build build = herokuDeployService.deployNode(apiKey, app, url);

        URL outputStreamUrl = new URL(build.getOutput_stream_url());
        herokuDeployService.log(outputStreamUrl);
    }

}
