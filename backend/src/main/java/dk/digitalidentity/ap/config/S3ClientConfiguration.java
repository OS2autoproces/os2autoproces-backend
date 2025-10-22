package dk.digitalidentity.ap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3ClientConfiguration {

    @Value("${upcloud.s3.endpoint}")
    private String endpoint;

	@Value("${upcloud.s3.credentials.accessKey}")
	private String accessKey;

	@Value("${upcloud.s3.credentials.secretKey}")
	private String secretKey;

    @Bean
    @Primary
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                endpoint, 
                "eu-central-1" // Dummy region, endpoint takes precedence
            ))
            .withCredentials(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey)
            ))
            .withPathStyleAccessEnabled(true)
            .build();
    }
}