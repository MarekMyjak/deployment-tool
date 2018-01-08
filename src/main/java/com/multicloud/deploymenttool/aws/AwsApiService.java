package com.multicloud.deploymenttool.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AwsApiService {
    private static final String APPLICATION_NAME = "docker-app";
    private static final String ENVIRONMENT_NAME = "docker-env";
    private static final String S3_BUCKET_NAME = "marekmyjak-my-bucket";
    private static final String SOLUTION_STACK_FOR_DOCKER = "64bit Amazon Linux 2017.09 v2.8.2 running Docker 17.06.2-ce";
    private static final String ZIP_FILE_NAME = "docker.zip";
    private static final String VERSION_LABEL = "v1";


    public void deployDocker(AWSCredentials awsCredentials, Regions region, File zipFile) {
        putFileIntoS3(awsCredentials, region, zipFile);
        AWSElasticBeanstalk client = createElasticBeanstalkClient(awsCredentials, region);

        client.createApplicationVersion(new CreateApplicationVersionRequest()
                .withApplicationName(APPLICATION_NAME)
                .withVersionLabel(VERSION_LABEL)
                .withAutoCreateApplication(true)
                .withSourceBundle(new S3Location()
                        .withS3Bucket(S3_BUCKET_NAME)
                        .withS3Key(ZIP_FILE_NAME)));
        client.createEnvironment(new CreateEnvironmentRequest()
                .withApplicationName(APPLICATION_NAME)
                .withEnvironmentName(ENVIRONMENT_NAME)
                .withSolutionStackName(SOLUTION_STACK_FOR_DOCKER));

        client.shutdown();
    }

    private void putFileIntoS3(AWSCredentials awsCredentials, Regions region, File zipFile) {
        AmazonS3 amazonS3 = createS3Client(awsCredentials, region);
        if (!amazonS3.doesBucketExistV2(S3_BUCKET_NAME)) {
            amazonS3.createBucket(S3_BUCKET_NAME);
        }
        amazonS3.putObject(S3_BUCKET_NAME, ZIP_FILE_NAME, zipFile);
    }

    public void deleteApplication(AWSCredentials awsCredentials, Regions region) {
        AWSElasticBeanstalk client = createElasticBeanstalkClient(awsCredentials, region);

        TerminateEnvironmentResult terminateEnvironmentResult = client.terminateEnvironment(new TerminateEnvironmentRequest().withEnvironmentName(ENVIRONMENT_NAME));

        client.deleteEnvironmentConfiguration(new DeleteEnvironmentConfigurationRequest(APPLICATION_NAME, ENVIRONMENT_NAME));
        client.deleteApplication(new DeleteApplicationRequest(APPLICATION_NAME));
        client.shutdown();
    }


    private AWSElasticBeanstalk createElasticBeanstalkClient(AWSCredentials awsCredentials, Regions region) {
        return AWSElasticBeanstalkClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    private AmazonS3 createS3Client(AWSCredentials awsCredentials, Regions region) {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
