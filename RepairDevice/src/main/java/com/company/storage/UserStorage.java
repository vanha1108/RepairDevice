package com.company.storage;

import java.util.HashMap;

public class UserStorage {

    private static UserStorage intance;

    private HashMap<String,String> users;

    private UserStorage(){
        users = new HashMap<String,String>();
    }

    public static synchronized UserStorage getInstance(){
        if(intance==null){
            intance = new UserStorage();
        }
        return intance;
    }

    public HashMap<String,String> getUsers(){
        return users;
    }

    public boolean setUsers(String code, String role){
        if(users.containsKey(code)) {
            return false;
        }
        users.put(code,role);
        return true;
    }

    public void removeUser(String code){
        try{
            users.remove(code);
        }catch(Exception e){

        }

    }
}