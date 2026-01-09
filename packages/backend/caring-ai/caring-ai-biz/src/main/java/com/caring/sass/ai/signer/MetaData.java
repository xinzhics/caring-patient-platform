package com.caring.sass.ai.signer;

import lombok.Data;

@Data
public class MetaData {
    private String algorithm;
    private String credentialScope;
    private String signedHeaders;
    private String date;
    private String region;
    private String service;
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public String getCredentialScope() {
		return credentialScope;
	}
	public void setCredentialScope(String credentialScope) {
		this.credentialScope = credentialScope;
	}
	public String getSignedHeaders() {
		return signedHeaders;
	}
	public void setSignedHeaders(String signedHeaders) {
		this.signedHeaders = signedHeaders;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
}
