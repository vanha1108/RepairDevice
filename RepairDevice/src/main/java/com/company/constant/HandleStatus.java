package com.company.constant;

import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class HandleStatus {
   // private ArrayList<Pair<String,String>> lst = new ArrayList<Pair<String,String>>();
  //  private ArrayList<Pair<String,String>> lstDone = new ArrayList<Pair<String,String>>();
    private HashMap<String,String> hlstApproveed = new HashMap<String,String>();
    private HashMap<String,String> hlstDone = new HashMap<String,String>();
    private HashMap<String,String> hlstBeforeHandle = new HashMap<String,String>();

    public HandleStatus() {
        hlstApproveed.put("EMPLOYEE","WAIT_MANAGER");
        hlstApproveed.put("MANAGER","WAIT_DIRECTOR");
        hlstApproveed.put("DIRECTOR","WAIT_TCHC");
        hlstApproveed.put("TCHC","FIXING");

        hlstDone.put("EMPLOYEE","FAILED");
        hlstDone.put("DIRECTOR","FINISHED");
        hlstDone.put("TCHC","FINISHED");

        hlstBeforeHandle.put("EMPLOYEE","WAIT_MANAGER");
        hlstBeforeHandle.put("MANAGER","WAIT_MANAGER");
        hlstBeforeHandle.put("DIRECTOR","WAIT_DIRECTOR");
        hlstBeforeHandle.put("TCHC","WAIT_TCHC");
    }

    public String getStatusBeforeHandle(String role){
        for (Map.Entry<String, String> entry : hlstBeforeHandle.entrySet()) {
            if(entry.getKey().equals(role)){
                return entry.getValue();
            }
        }
        return "";
    }

    public String getStatusApproved(String role){
        for (Map.Entry<String, String> entry : hlstApproveed.entrySet()) {
            if(entry.getKey().equals(role)){
                return entry.getValue();
            }
        }
        return "";
    }
    public  String getStatusHandled(String role){
        for (Map.Entry<String, String> entry : hlstDone.entrySet()) {
            if(entry.getKey().equals(role)){
                return entry.getValue();
            }
        }
        return "";
    }

    public String getRoleByStatusRequest(String status){
        if(status.equals("WAIT_MANAGER")){
            return "MANAGER";
        }
        for (Map.Entry<String, String> entry : hlstBeforeHandle.entrySet()) {
            if(entry.getValue().equals(status)){
                return entry.getKey();
            }
        }
        return "null";
    }

}
