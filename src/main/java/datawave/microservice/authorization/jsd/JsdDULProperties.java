package datawave.microservice.authorization.jsd;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JsdDULProperties.class)
@ConfigurationProperties(prefix = "jsd.users")
public class JsdDULProperties {
    private String dbAddr = "keycloak-jsd-db.datawave.svc.cluster.local:5432";
    private String dbName = "keycloak";
    private String dbUser = "dbusername";
    private String dbPassword = "dbpassword";
    private boolean clearancesEnabled = true;
    private boolean disseminationControlsEnabled = true;
    private boolean fineAccessControlsEnabled = true;
    private boolean releasableTosEnabled = true;
    private boolean classRolesEnabled = true;
    private boolean useCacFormat = false;
    private boolean rolesEnabled = false;
    private String serverDnRegex;
    
    public boolean isUseCacFormat() {
        return useCacFormat;
    }
    
    public void setUseCacFormat(boolean useCacFormat) {
        this.useCacFormat = useCacFormat;
    }
    
    public boolean isClassRolesEnabled() {
        return classRolesEnabled;
    }
    
    public void setClassRolesEnabled(boolean classRolesEnabled) {
        this.classRolesEnabled = classRolesEnabled;
    }
    
    public String getDbAddr() {
        return dbAddr;
    }
    
    public void setDbAddr(String dbAddr) {
        this.dbAddr = dbAddr;
    }
    
    public String getDbName() {
        return dbName;
    }
    
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public String getDbUser() {
        return dbUser;
    }
    
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }
    
    public String getDbPassword() {
        return dbPassword;
    }
    
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
    
    public boolean isClearancesEnabled() {
        return clearancesEnabled;
    }
    
    public void setClearancesEnabled(boolean enableClearances) {
        this.clearancesEnabled = enableClearances;
    }
    
    public boolean isDisseminationControlsEnabled() {
        return disseminationControlsEnabled;
    }
    
    public void setDisseminationControlsEnabled(boolean enableDisseminationControls) {
        this.disseminationControlsEnabled = enableDisseminationControls;
    }
    
    public boolean isFineAccessControlsEnabled() {
        return fineAccessControlsEnabled;
    }
    
    public void setFineAccessControlsEnabled(boolean enablefineAccessControls) {
        this.fineAccessControlsEnabled = enablefineAccessControls;
    }
    
    public boolean isReleasableTosEnabled() {
        return releasableTosEnabled;
    }
    
    public void setReleasableTosEnabled(boolean enablereleasableTos) {
        this.releasableTosEnabled = enablereleasableTos;
    }
    
    public boolean isRolesEnabled() {
        return rolesEnabled;
    }
    
    public void setRolesEnabled(boolean rolesEnabled) {
        this.rolesEnabled = rolesEnabled;
    }
    
    public String getServerDnRegex() {
        return serverDnRegex;
    }
    
    public void setServerDnRegex(String serverDnRegex) {
        this.serverDnRegex = serverDnRegex;
    }
    
}
