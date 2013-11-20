package com.example.ndndroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InitNDN {
    public static String StartNDN() {
		Process p, p2, p3;  
		String result = null;
		try {  
           // Perform su to get root privileges
           p = Runtime.getRuntime().exec("/system/bin/chmod 777 /data/data/com.example.ndndroid/ndnld");
           p.waitFor();
           
           p = Runtime.getRuntime().exec("/system/bin/chmod 777 /data/data/com.example.ndndroid/ndnldc");
           p.waitFor();
          
           p2 = Runtime.getRuntime().exec(new String[]{"su","-c", "/data/data/com.example.ndndroid/ndnld"});
           

           BufferedReader stdError = new BufferedReader(new InputStreamReader(p2.getErrorStream()));
           
           p3 = Runtime.getRuntime().exec(new String[]{"ps","|", "grep","ndnld"});
           BufferedReader stdInput = new BufferedReader(new InputStreamReader(p3.getInputStream()));
           
           // read the output from the command
           String s;
           
                  
           boolean found = false;
           
           while ((s = stdInput.readLine()) != null ) {
         	           	  
         	  if (s.contains("/data/data/com.example.ndndroid/ndnld")) {
         		  found = true;
         	      break;
         	  }
         	  
           }
           
           // read any errors from the attempted command
           if (found == false) {
        	  result = new String(); 
         	  stdInput = new BufferedReader(new InputStreamReader(p2.getInputStream()));
         	  while ((s = stdError.readLine()) != null) {
             	  result.concat(s);                		  
         	  }
           }

          
         
        } catch (IOException e) {  
           // TODO Code to run in input/output exception  
        } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
		return result;		
    }
    
    
}
