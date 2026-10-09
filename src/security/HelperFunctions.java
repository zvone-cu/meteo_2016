package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.jfree.util.Log;


public class HelperFunctions {
	
	static Logger logger = Logger.getLogger(HelperFunctions.class);

	/**
	 * returns sha-256 hex string
	 * @param pwd
	 * @return String
	 * @throws NoSuchAlgorithmException
	 */
	public static String sha2(String pwd)  {
		
		MessageDigest digest;
		
		try {
			digest = MessageDigest.getInstance("SHA-256");
		
		} catch (NoSuchAlgorithmException e) {
			
			Log.error(e.getMessage());
			return null;
			
		}
		byte[] hash = digest.digest( pwd.getBytes(StandardCharsets.UTF_8));

		    StringBuilder hexString = new StringBuilder(2 * hash.length);
		    for (int i = 0; i < hash.length; i++) {
		        String hex = Integer.toHexString(0xff & hash[i]);
		        if(hex.length() == 1) {
		            hexString.append('0');
		        }
		        hexString.append(hex);
		    }
		    
		    logger.info("sha2="+hexString.toString() );
		    
		    return hexString.toString();
	}
	
}
