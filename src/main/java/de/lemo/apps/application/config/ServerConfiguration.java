package de.lemo.apps.application.config;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.lemo.apps.entities.User;

/**
 * 
 * 
 * @author Leonard Kappe
 * 
 */
public enum ServerConfiguration {

    INSTANCE;

    {
        // TODO the DMS initializes the logger here - is there a better way in tapestry?
    }

    private static String USER_FILE = "users.xml";

    private Logger logger = Logger.getLogger(getClass());
    private String serverName;
    private Map<String, String> dbConfig;
    private String dmsUrl;

    private List<User> userImports;

    public static ServerConfiguration getInstance() {
        return INSTANCE;
    }

    /**
     * The context path is used to determine the configuration file's name. The first char (a slash by convention) will be
     * ignored. Some context paths aren't valid file names (like the root context or those with sub-paths), so tomcat's
     * naming conventions for .war files will be used.
     * 
     * Examples:
     * 
     * <pre>
     * context   -> file name
     * -------------------------
     * /         -> ROOT.xml
     * /lemo     -> lemo.xml
     * /lemo/foo -> lemo#foo.xml
     * </pre>
     * 
     * 
     * @param contextPath
     */
    public void loadConfig(String contextPath) {
        LemoConfig lemoConfig = readConfigFiles(contextPath);
        // TODO set log level from config

        serverName = lemoConfig.applicationServer.name;
        dmsUrl = lemoConfig.applicationServer.dataManagementServerURL;

        dbConfig = Maps.newHashMap();
        for(PropertyConfig property : lemoConfig.applicationServer.appDbConfig) {
            dbConfig.put(property.key, property.value);
        }
        LemoUserConfig userConfigurations = readUserFile();
        if(userConfigurations != null) {
            userImports = createUsers(userConfigurations);
        } else {
            userImports = Lists.newArrayList();
        }
    }

    private List<User> createUsers(LemoUserConfig userConfigurations) {
        List<User> users = Lists.newArrayList();
        if(userConfigurations.users == null || userConfigurations.users.isEmpty()) {
            logger.warn(USER_FILE + " loaded but no users elements were found.");
        } else {
            for(UserConfig userConfig : userConfigurations.users) {
                User user = new User(userConfig.fullName, userConfig.username, userConfig.email, userConfig.password);
                user.setMyCourses(userConfig.courses);
                users.add(user);
            }
        }
        return users;
    }

    private LemoUserConfig readUserFile() {
        try {
            Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(LemoUserConfig.class).createUnmarshaller();
            InputStream in = getClass().getResourceAsStream("/" + USER_FILE);
            if(in == null) {
                return null;
            }
            logger.info("Loading user file: " + USER_FILE);
            return (LemoUserConfig) jaxbUnmarshaller.unmarshal(in);
        } catch (JAXBException e) {
            // no way to recover, re-throw at runtime
            throw new RuntimeException(e);
        }
    }

    private LemoConfig readConfigFiles(String contextPath) {
        if(contextPath.isEmpty()) {
            // empty means root context, Tomcat convention for root context war files is 'ROOT.war'
            contextPath = "/ROOT";
        }

        // remove leading slash, replace any slashes with hashes like Tomcat does with the war files
        String warName = contextPath.substring(1).replace('/', '#');

        Set<String> fileNames = new LinkedHashSet<String>();
        fileNames.add(warName + ".xml"); // default, based on war name

        int lastHash;
        String warPath = warName;
        while((lastHash = warPath.lastIndexOf('#')) > 0) {
            // try to read more generic name by removing sub-paths, i.e. /lemo/dms -> /lemo
            // (useful to read the appserver's config file, so only one file is needed)
            warPath = warPath.substring(0, lastHash);
            fileNames.add(warPath + ".xml");
        }

        fileNames.add("lemo.xml"); // eventually try generic lemo.xml for use in local development

        LemoConfig lemoConfig = null;
        try {
            Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(LemoConfig.class).createUnmarshaller();
            for(String fileName : fileNames) {
                InputStream in = getClass().getResourceAsStream("/" + fileName);
                if(in != null) {
                    logger.info("Using config file: " + fileName);
                    lemoConfig = (LemoConfig) jaxbUnmarshaller.unmarshal(in);
                }
            }
        } catch (JAXBException e) {
            // no way to recover, re-throw at runtime
            throw new RuntimeException(e);
        }
        if(lemoConfig == null) {
            String files = fileNames.toString();
            throw new RuntimeException(
                    "No config file found in the classpath. Files follow tomcat's naming convention for .war files, " +
                            "in following order until a valid one is found: "
                            + files.substring(1, files.length() - 1));
        }

        logger.info("Config loaded for '" + lemoConfig.applicationServer.name + "'");
        return lemoConfig;
    }

    public String getName() {
        return serverName;
    }

    public String getDMSBaseUrl() {
        return dmsUrl;
    }

    public Map<String, String> getDbConfig() {
        return dbConfig;
    }

    public List<User> getUserImports() {
        return userImports;
    }

}