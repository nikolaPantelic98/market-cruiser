package com.marketcruiser.common.entity;

/**
 * The Constants class provides access to common constants used in the application.
 * It includes the base URI for Amazon S3 storage.
 */
public class Constants {

    /**
     * The base URI for accessing Amazon S3 storage.
     * It is constructed using the AWS_BUCKET_NAME and AWS_REGION environment variables.
     * The format of the URI is "https://{bucketName}.s3.{region}.amazonaws.com".
     */
    public static final String S3_BASE_URI;

    static {
        String bucketName = System.getenv("AWS_BUCKET_NAME");
        String region = System.getenv("AWS_REGION");
        String pattern = "https://%s.s3.%s.amazonaws.com";

        String uri = String.format(pattern, bucketName, region);

        S3_BASE_URI = bucketName == null ? "" : uri;
    }
}
