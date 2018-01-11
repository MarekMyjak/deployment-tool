package com.multicloud.deploymenttool.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.S3Location;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AwsDeployService {
    private static final String SOLUTION_STACK_FOR_DOCKER = "64bit Amazon Linux 2017.09 v2.8.2 running Docker 17.06.2-ce";

    public void deployDocker(File zipFile, AwsDeployConfiguration awsDeployConfiguration) {
        putFileIntoS3(awsDeployConfiguration, zipFile);
        AWSElasticBeanstalk client = createElasticBeanstalkClient(awsDeployConfiguration);
        createApplicationVersion(zipFile, awsDeployConfiguration, client);
        createEnvironment(awsDeployConfiguration, client);
        client.shutdown();
    }

    private void createEnvironment(AwsDeployConfiguration awsDeployConfiguration, AWSElasticBeanstalk client) {
        client.createEnvironment(new CreateEnvironmentRequest()
                .withApplicationName(awsDeployConfiguration.getApplicationName())
                .withVersionLabel(awsDeployConfiguration.getVersionLabel())
                .withEnvironmentName(awsDeployConfiguration.getEnvironmentName())
                .withSolutionStackName(SOLUTION_STACK_FOR_DOCKER)
                .withOptionSettings(awsDeployConfiguration.getConfigurationOptionSettings()));
    }

    private void createApplicationVersion(File zipFile, AwsDeployConfiguration awsDeployConfiguration, AWSElasticBeanstalk client) {
        client.createApplicationVersion(new CreateApplicationVersionRequest()
                .withApplicationName(awsDeployConfiguration.getApplicationName())
                .withVersionLabel(awsDeployConfiguration.getVersionLabel())
                .withAutoCreateApplication(true)
                .withSourceBundle(new S3Location()
                        .withS3Bucket(awsDeployConfiguration.getS3BucketName())
                        .withS3Key(zipFile.getName())));
    }

    private void putFileIntoS3(AwsDeployConfiguration awsDeployConfiguration, File zipFile) {
        AmazonS3 amazonS3 = createS3Client(awsDeployConfiguration);
        String s3BucketName = awsDeployConfiguration.getS3BucketName();
        if (!amazonS3.doesBucketExistV2(s3BucketName)) {
            amazonS3.createBucket(s3BucketName);
        }
        amazonS3.putObject(s3BucketName, zipFile.getName(), zipFile);
    }

    private AWSElasticBeanstalk createElasticBeanstalkClient(AwsDeployConfiguration awsDeployConfiguration) {
        return AWSElasticBeanstalkClientBuilder
                .standard()
                .withRegion(awsDeployConfiguration.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsDeployConfiguration.getAwsCredentials()))
                .build();
    }

    private AmazonS3 createS3Client(AwsDeployConfiguration awsDeployConfiguration) {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(awsDeployConfiguration.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsDeployConfiguration.getAwsCredentials()))
                .build();
    }
}
