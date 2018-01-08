package com.multicloud.deploymenttool.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AwsTest {

    @Autowired
    private AwsApiService awsApiService;

    /**
     * This test requires the credentials to be saved in the AWS credentials file
     * <p> @see https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
     *
     * @see ProfileCredentialsProvider
     */
    @Test
    public void dummyTest() throws IOException {
//        given
        Regions region = Regions.EU_WEST_1;
        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        File file = new ClassPathResource("docker.zip").getFile();
//        when
        awsApiService.deployDocker(credentials, region, file);
    }
}
