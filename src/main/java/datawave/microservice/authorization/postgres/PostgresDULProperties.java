package datawave.microservice.authorization.postgres;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(PostgresDULProperties.class)
@ConfigurationProperties(prefix = "postgres.config")
public class PostgresDULProperties {
    private String dbAddr;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private boolean rolesEnabled = true;
    private String serverDnRegex;
    private String cnRegex = "cn=(?<sn>[a-zA-Z.0-9]*),?";
    private String userTable = "user_information";
    private String idColumn = "id";
    private Map<String,String> idQueryWhereClauses;
    private String dnRegex;
    
    public String getDnRegex() {
        return dnRegex;
    }
    
    public void setDnRegex(String dnRegex) {
        this.dnRegex = dnRegex;
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
    
    public Map<String,String> getIdQueryWhereClauses() {
        return idQueryWhereClauses;
    }
    
    public void setIdQueryWhereClauses(Map<String,String> idQueryWhereClauses) {
        this.idQueryWhereClauses = idQueryWhereClauses;
    }
    
    public String getUserTable() {
        return userTable;
    }
    
    public void setUserTable(String userTable) {
        this.userTable = userTable;
    }
    
    public String getIdColumn() {
        return idColumn;
    }
    
    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }
    
    public String getCnRegex() {
        return cnRegex;
    }
    
    public void setCnRegex(String cnRegex) {
        this.cnRegex = cnRegex;
    }
    
}
