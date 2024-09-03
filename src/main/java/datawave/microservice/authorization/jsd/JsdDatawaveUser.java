package datawave.microservice.authorization.jsd;

import java.util.HashMap;
import java.util.Map;

public class JsdDatawaveUser {
    
    private Map<String,String> rolesToAuths = new HashMap<>();
    private String email;
    
    public void setRolesToAuths(Map<String,String> rolesToAuths) {
        this.rolesToAuths = rolesToAuths;
    }
    
    public Map<String,String> getRolesToAuths() {
        return rolesToAuths;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
}
