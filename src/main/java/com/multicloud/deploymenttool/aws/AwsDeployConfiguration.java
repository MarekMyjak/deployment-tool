package com.multicloud.deploymenttool.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwsDeployConfiguration {
    private final AWSCredentials awsCredentials;
    private final Regions region;
    private final ConfigurationOptionSetting[] configurationOptionSettings;
    private final String applicationName;
    private final String environmentName;
    private final String s3BucketName;
    private final String versionLabel;
}
