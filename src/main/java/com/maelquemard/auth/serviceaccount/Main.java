package com.maelquemard.auth.serviceaccount;

public class Main {

	public static void main(String[] args) throws Throwable {
		if (args.length >= 2) {			
			// Required to get file of service account
			String pathToJsonFile = args[0];
			// Required to specified scopes of API
			String scopeAPI = args[1];
			
			try {
				System.out.println(GoogleAuthServiceAccount.GetAccessTokenFromJson(pathToJsonFile, scopeAPI));
			} catch (Throwable t) {
				System.out.println("Failure during static initialization" + t.toString());
				throw t;
			}
		} else {
			throw new Exception("Arguments path to json service account (first args) and scope (second args) is required\n path example: /service_account_file.json\n scope example: https://www.googleapis.com/auth/indexing");
		}
	}

}
