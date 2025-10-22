package dk.digitalidentity.ap.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3Service {

	@Value("${upcloud.s3.bucket.url}")
	private String s3URLPrefix;

	@Value("${upcloud.s3.bucket.name}")
	private String bucketName;
	
	@Value("${upcloud.s3.credentials.accessKey}")
	private String accessKey;

	@Autowired
	private AmazonS3 amazonS3Client;

    public String writeFile(InputStream inputStream, String fileName) {
        try {
			String uniqueFileName = generateRandomFileName(fileName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            metadata.setContentType("application/octet-stream");

            PutObjectRequest request = new PutObjectRequest(bucketName, uniqueFileName, inputStream, metadata);
            amazonS3Client.putObject(request);
            
			return s3URLPrefix + uniqueFileName;
        }
        catch (Exception ex) {
			if (accessKey == null || accessKey.length() == 0) {
				log.warn("No AWS/S3 credentials configured, not storing attachment");
			}
			else {
				log.warn("Unable to store attachment in S3", ex);
			}
			
			return null;
        }
    }

	public byte[] downloadAsBytes(String key) throws IOException {

		if (bucketName == null) {
			log.debug("Not found. Bucket: " + bucketName + " Key: " + key);
			return null;
		}

		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
		S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
		return org.apache.commons.io.IOUtils.toByteArray(s3Object.getObjectContent());
	}

	public void delete(String fileName) {
		amazonS3Client.deleteObject(bucketName, fileName);
	}

	private String generateRandomFileName(String fileName) {
		String fileExtension = getFileExtension(fileName);
		String filenameWithoutExtension = getFilenameWithoutExtension(fileName);
		String randomSuffix = Long.toString(System.currentTimeMillis());

		if (!StringUtils.hasLength(fileExtension)) {
			return filenameWithoutExtension + "-" + randomSuffix;
		}
		else {
			return filenameWithoutExtension + "-" + randomSuffix + "." + fileExtension;
		}
	}

	private String getFileExtension(String name) {
		if (name.lastIndexOf(".") < 0) {
			return "";
		}

		return name.substring(name.lastIndexOf(".") + 1);
	}
	
	private String getFilenameWithoutExtension(String name) {
		if (name.lastIndexOf(".") < 0) {
			return name;
		}

		return name.substring(0, name.lastIndexOf("."));
	}
}
