package com.caring.sass.ai.signer;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Credentials {

    @JSONField(name = "ak")
    private String accessKeyID;
    @JSONField(name = "sk")
    private String secretAccessKey;
    private String service;
    private String region;

    public Credentials() {
    }

    public Credentials(String region, String service) {
        this.region = region;
        this.service = service;
    }

	public String getAccessKeyID() {
		return accessKeyID;
	}

	public void setAccessKeyID(String accessKeyID) {
		this.accessKeyID = accessKeyID;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
    
    
}
