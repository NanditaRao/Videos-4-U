package com.columbia.db;

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;


public class CreateBucket {

	/**
	 * @param args
	 * @throws IOException 
	 * Create a bucket for the videos if it does exist already
	 */
	public static void main(String[] args) throws IOException {
		
	   	 AWSCredentials credentials = new PropertiesCredentials(
				 CreateBucket.class.getResourceAsStream("AwsCredentials.properties"));
	   	AmazonS3Client s3 = new AmazonS3Client(credentials);
		
        String bucketName = "ra2616.cloud2.bucket";
		s3.createBucket(bucketName);

	}

}
