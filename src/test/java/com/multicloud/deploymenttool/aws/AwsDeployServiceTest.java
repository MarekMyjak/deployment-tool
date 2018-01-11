package com.multicloud.deploymenttool.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
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
public class AwsDeployServiceTest {

    private static final String DOCKER_FILE = "docker.zip";
    private static final String APPLICATION_NAME = "docker-app";
    private static final String ENVIRONMENT_NAME = "docker-env";
    private static final String S3_BUCKET_NAME = "marekmyjak-my-bucket";
    private static final String VERSION_LABEL = "v1";
    private static final Regions REGION = Regions.EU_WEST_1;

    @Autowired
    private AwsDeployService awsDeployService;

    /**
     * This test requires the credentials to be saved in the AWS credentials file
     * <p> @see https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
     *
     * @see ProfileCredentialsProvider
     */
    @Test
    public void deployTest() throws IOException {
//        given
        File file = new ClassPathResource(DOCKER_FILE).getFile();
        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        AwsDeployConfiguration awsDeployConfiguration = AwsDeployConfiguration
                .builder()
                .awsCredentials(credentials)
                .region(REGION)
                .configurationOptionSettings(getConfigurationOptionSettings())
                .applicationName(APPLICATION_NAME)
                .environmentName(ENVIRONMENT_NAME)
                .versionLabel(VERSION_LABEL)
                .s3BucketName(S3_BUCKET_NAME)
                .build();
//        when
        awsDeployService.deployDocker(file, awsDeployConfiguration);
    }

    private static ConfigurationOptionSetting[] getConfigurationOptionSettings() {
        return new ConfigurationOptionSetting[]{
                new ConfigurationOptionSetting()
                        .withNamespace("aws:autoscaling:launchconfiguration")
                        .withOptionName("InstanceType")
                        .withValue("t2.micro"),
                new ConfigurationOptionSetting()
                        .withNamespace("aws:elasticbeanstalk:environment")
                        .withOptionName("EnvironmentType")
                        .withValue("SingleInstance")};
    }
}
