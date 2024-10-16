package datawave.microservice.authorization.postgres;

import static datawave.microservice.authorization.jsd.JsdDatawaveUserService.CACHE_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import datawave.security.authorization.DatawaveUser;
import datawave.security.authorization.DatawaveUser.UserType;
import datawave.security.authorization.SubjectIssuerDNPair;

/**
 * A helper class to allow calls to be cached using Spring annotations. Normally, these could just be methods in {@link PostgresDatawaveUserService}. However,
 * Spring caching works by wrapping the class in a proxy, and self-calls on the class will not go through the proxy. By having a separate class with these
 * methods, we get a separate proxy that performs the proper cache operations on these methods.
 */
@CacheConfig(cacheNames = CACHE_NAME)
public class PostgresDatawaveUserLookup {
    public final Logger logger = LoggerFactory.getLogger(getClass());
    private final PostgresDULProperties postgresDULProperties;
    
    public PostgresDatawaveUserLookup(PostgresDULProperties postgresDULProperties) {
        this.postgresDULProperties = postgresDULProperties;
    }
    
    @Cacheable(key = "#dn.toString()")
    public DatawaveUser lookupUser(SubjectIssuerDNPair dn) {
        return buildUser(dn);
    }
    
    @Cacheable(key = "#dn.toString()")
    public DatawaveUser reloadUser(SubjectIssuerDNPair dn) {
        return buildUser(dn);
    }
    
    private DatawaveUser buildUser(SubjectIssuerDNPair dn) {
        logger.info("Inside postgres build user...");
        logger.info(String.format("Database Address:%s", postgresDULProperties.getDbAddr()));
        logger.info(String.format("Database Name:%s", postgresDULProperties.getDbName()));
        UserType userType = UserType.USER;
        if (postgresDULProperties.getServerDnRegex() != null && Pattern.matches(postgresDULProperties.getServerDnRegex(), getCN(dn.subjectDN()))) {
            userType = UserType.SERVER;
        }
        
        List<String> auths = new ArrayList<String>();
        List<String> roles = new ArrayList<String>();
        
        try (Connection connection = getConnection()) {
            logger.info("Got connection, performing query...");
            
            int personGuid = getPersonGuid(connection, dn, userType);
            
            // no user found matching the cert, cannot continue
            if (personGuid == -1) {
            	logger.warn(String.format("%s User not found, cannot authorize.", dn.subjectDN()));
                return DatawaveUser.ANONYMOUS_USER;
            }
            
            List<String> myAuths = getAuths(connection, personGuid);
            auths.addAll(myAuths);
            
            // Table/list of Datawave roles associated with the user
            if (postgresDULProperties.isRolesEnabled()) {
                List<String> myRoles = getRoles(connection, personGuid);
                roles.addAll(myRoles);
            }
            
        } catch (SQLException e) {
            logger.warn(e.toString());
        }
        
        long creationTime = System.currentTimeMillis();
        long expireTime = creationTime + 60000;
        // Empty string in third variable slot is the email option
        return new DatawaveUser(dn, userType, "", auths, roles, null, creationTime, expireTime);
    }
    
    protected String getCN(String subjectDN) {
        logger.info("Pulling CN out from subject DN...");
        logger.info(subjectDN);
        logger.info(postgresDULProperties.getCnRegex());
        Pattern pattern = Pattern.compile(postgresDULProperties.getCnRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subjectDN);
        
        boolean matchFound = matcher.find();
        if (matchFound) {
            String match = matcher.group("sn");
            return match;
        } else {
            logger.warn("CN match not found");
        }
        
        throw new RuntimeException("No 'cn' found within certificate DN, cannot get user info.");
    }
    
    /**
     * Create the connection with the Postgresql Database
     * 
     * @return
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        logger.info("creating connection to DB...");
        String url = String.format("jdbc:postgresql://%s/%s", postgresDULProperties.getDbAddr(), postgresDULProperties.getDbName());
        Properties props = new Properties();
        props.setProperty("user", postgresDULProperties.getDbUser());
        props.setProperty("password", postgresDULProperties.getDbPassword());
        
        return DriverManager.getConnection(url, props);
    }
    
    /**
     * Gets the person GUID using the subject CN field of the certificate. Currently comparing to the first name of the user.
     * 
     * @param connection
     * @param dn
     *            - certificate information
     * @return The integer representing the personGuid
     * @throws SQLException
     */
    private int getPersonGuid(Connection connection, SubjectIssuerDNPair dn, UserType userType) throws SQLException {
        logger.info("Getting the person GUID...");
        // This is intended to add a sub-query string, depending on route being used.
        String query = buildIdQuery();
        logger.info(String.format("Database Query:%s", query));
        
        // TODO get first name from dn
        logger.info(String.format("DN Regex: %s", postgresDULProperties.getDnRegex()));
        logger.info(String.format("String to Match On: %s", getCN(dn.subjectDN())));
        Pattern pattern = Pattern.compile(postgresDULProperties.getDnRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getCN(dn.subjectDN()));
        matcher.find();
        logger.info(String.format("Count of Groups: %s", matcher.groupCount()));
        
        int personGuid = -1;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int idx = 1;
            for (String v : postgresDULProperties.getIdQueryWhereClauses().values()) {
                logger.info(String.format("Value: %s", v));
                stmt.setString(idx++, matcher.group(v));
            }
            logger.info(stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                personGuid = rs.getInt(postgresDULProperties.getIdColumn());
            }
            
            return personGuid;
        }
    }
    
    private String buildIdQuery() {
        String queryTemplate = String.format("select %s from %s", postgresDULProperties.getIdColumn(), postgresDULProperties.getUserTable());
        
        queryTemplate = queryTemplate.concat(" WHERE");
        Iterator<String> it = postgresDULProperties.getIdQueryWhereClauses().keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            queryTemplate = queryTemplate.concat(" ");
            queryTemplate = queryTemplate.concat(String.format("LOWER(%s) = (?)", key));
            if (it.hasNext()) {
                queryTemplate = queryTemplate.concat(" AND");
            }
        }
        
        return queryTemplate;
    }
    
    /**
     * Executes the query
     * 
     * @param conn
     * @param personGuid
     * @param table
     * @param column
     * @return
     * @throws SQLException
     */
    private List<String> executeQuery(Connection conn, int personGuid, String table, String column) throws SQLException {
        String query = String.format("select %s from %s where user_id = ?;", column, table);
        logger.info(String.format("Database Query:%s", query));
        List<String> values = new ArrayList<String>();
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, personGuid);
            logger.info(stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String value = rs.getString(column);
                values.add(value);
            }
        }
        return values;
    }
    
    private List<String> getRoles(Connection conn, int personGuid) throws SQLException {
        logger.info("Getting the list of user roles...");
        return executeQuery(conn, personGuid, "dw_roles", "dw_role");
    }
    
    private List<String> getAuths(Connection conn, int personGuid) throws SQLException {
        logger.info("Getting the list of user auths...");
        return executeQuery(conn, personGuid, "auths", "auth");
    }
}
