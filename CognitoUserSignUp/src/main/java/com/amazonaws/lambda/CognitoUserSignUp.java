package com.amazonaws.lambda;

import java.util.LinkedHashMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CognitoUserSignUp implements RequestHandler<Object, String> {

	private String AccessKey = "AKIAYUCMZEDM5Q3UW7ZH";
	private String SecretKey = "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";

	private String AppClientId = "6t097jurf8m5rhj6gh2cklmik8";

	@Override
	public String handleRequest(Object input, Context context) {
		context.getLogger().log("Input: " + input);

		try {

//			fetching the user details from event 

			LinkedHashMap map = (LinkedHashMap) input;
			LinkedHashMap details = (LinkedHashMap) map.get("detail");

			String SupervisorName = (String) details.get("SupervisorName");
			String emailId = (String) details.get("emailId");
			String password = (String) details.get("password");
			String SupervisorId = (String) details.get("SupervisorId");
			String SupervisorLevel = (String) details.get("SupervisorLevel");
			String OrgId = (String) details.get("OrgId");
			String userName = (String)details.get("userName");
			
			context.getLogger().log(userName + "  " + emailId + "  " + password + "  " + SupervisorId + " " + SupervisorLevel + " " + OrgId);

			AWSCredentials cred = new BasicAWSCredentials(AccessKey, SecretKey);
			AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider(cred);
			AWSCognitoIdentityProvider client = AWSCognitoIdentityProviderClientBuilder.standard()
					.withCredentials(credProvider).withRegion(Regions.AP_SOUTH_1).build();
			context.getLogger().log("Cognito Client created !");

//			creating attribute to store the emailId attribute of the user 
			AttributeType[] attribute = new AttributeType[5];

			AttributeType email = new AttributeType();
			email.setName("email");
			email.setValue(emailId);

			AttributeType OrgIdAttribute = new AttributeType();
			OrgIdAttribute.setName("custom:OrgId");
			OrgIdAttribute.setValue(OrgId);

			AttributeType SupervisorIdAttribute = new AttributeType();
			SupervisorIdAttribute.setName("custom:SupervisorId");
			SupervisorIdAttribute.setValue(SupervisorId);

			AttributeType SupervisorLevelAttribute = new AttributeType();
			SupervisorLevelAttribute.setName("custom:SupervisorLevel");
			SupervisorLevelAttribute.setValue(SupervisorLevel);

			AttributeType SupervisorNameAttribute = new AttributeType();
			SupervisorNameAttribute.setName("custom:SupervisorName");
			SupervisorNameAttribute.setValue(SupervisorName);

			attribute[0] = email;
			attribute[1] = OrgIdAttribute;
			attribute[2] = SupervisorIdAttribute;
			attribute[3] = SupervisorLevelAttribute;
			attribute[4] =  SupervisorNameAttribute;

			context.getLogger().log("attributes of user set up !");
//			cognito Sign Up request set up

			SignUpRequest request = new SignUpRequest().withClientId(AppClientId).withUsername(userName)
					.withPassword(password).withUserAttributes(attribute);

			context.getLogger().log("request for signup is setUp");

			SignUpResult result = client.signUp(request);

			result.setUserConfirmed(true);

			context.getLogger().log("user signed up successfully !");

			return "user signed up successfully !";

		} catch (Exception e) {
			context.getLogger().log("Exception occured :-  " + e.getMessage());

			return "Exception occured :-  " + e.getMessage();

		}

	}

}
