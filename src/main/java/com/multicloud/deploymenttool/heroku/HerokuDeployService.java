package com.multicloud.deploymenttool.heroku;

import com.heroku.api.App;
import com.heroku.api.Build;
import com.heroku.api.Heroku;
import com.heroku.api.HerokuAPI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
@Service
public class HerokuDeployService {

    public App createApp(String apiKey) {
        return new HerokuAPI(apiKey).createApp().on(Heroku.Stack.Heroku16);
    }

    public App createNamedApp(String apiKey, String name) {
        return new HerokuAPI(apiKey).createApp().on(Heroku.Stack.Heroku16).named(name);
    }


    public Build deployNode(String apiKey, App app, String url) {
        final HerokuAPI api = new HerokuAPI(apiKey);
        return api.createBuild(
                app.getName(),
                new Build(url, null, new String[]{}));
    }

    @SneakyThrows
    public void log(URL outputStreamUrl) {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(outputStreamUrl.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            log.debug(inputLine);
        in.close();
    }

}
