package com.multicloud.deploymenttool.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "aws")
public class AwsController {

    private final AwsApiService awsApiService;

    @Autowired
    public AwsController(AwsApiService awsApiService) {
        this.awsApiService = awsApiService;
    }

    @GetMapping(value = "")
    @ResponseBody
    public String hello() {
        AWSCredentials awsCredentials = new BasicAWSCredentials("access_key_id", "secret_key_id");
//        awsApiService.deployDocker();
        return "Hello World!";
    }
}
