package com.maelquemard.auth.serviceaccount;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.auth.Credentials;
import com.google.auth.oauth2.JwtClaims;
import com.google.auth.oauth2.JwtCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

public class GoogleAuthServiceAccount {
	
	/**
	 * {@summary Function to get Access Token Google available for 3600 seconds}
	 * @param String scope, scope for access token (ex: "https://www.googleapis.com/auth/indexing")
	 * @param String Path, Relative or absolute path to service account json
	 * @return String json with access token
	 * @throws IOException
	 */
	public static String GetAccessTokenFromJson(String path, String scopes) throws IOException {

		// Load json contains informations about services account
		InputStream in = new FileInputStream(path);

		// Load service account to get credentials from file
		ServiceAccountCredentials saCreds = ServiceAccountCredentials.fromStream(in);

		// Added scope before claims JWT
		Map<String, String> additionalClaims = new HashMap<String, String>();
		additionalClaims.put("scope", scopes);

		/* 
		 * Get JWTClaims
		 * All informations is obtain from credential previously loaded
		 * aud: token uri
		 * iss: service account email
		 * sub: service account email
		 * Additional Claims : scope
		 */
		JwtClaims claims = JwtClaims.newBuilder()
				.setAudience(saCreds.getTokenServerUri().toString())
				.setIssuer(saCreds.getClientEmail())
				.setSubject(saCreds.getClientEmail())
				.setAdditionalClaims(additionalClaims).build();

		// Sign Jwt with private key from credential previously loaded
		Credentials c = JwtCredentials.newBuilder()
				.setPrivateKey(saCreds.getPrivateKey())
				.setPrivateKeyId(saCreds.getPrivateKeyId())
				.setJwtClaims(claims).build();

		// Get jwt sign to call token uri
		Map<String, List<String>> result = c.getRequestMetadata(saCreds.getTokenServerUri());
		String jwtSign = "";
		if (!result.isEmpty()) {			
			List<String> auth = result.get("Authorization");
			
			if (!auth.isEmpty())
				jwtSign = auth.get(0).replace("Bearer ", "");
		}

		// Prepare Http POST with token uri
		HttpPost post = new HttpPost(saCreds.getTokenServerUri());

		/*
		 *  add request parameter, form parameters
		 *  grant_type: urn:ietf:params:oauth:grant-type:jwt-bearer
		 *  assertion: jwt sign
		 */
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
		urlParameters.add(new BasicNameValuePair("assertion", jwtSign));
		
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		// Call http
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
			
			// Get response of call http
			return EntityUtils.toString(response.getEntity());
		}
	}

}
