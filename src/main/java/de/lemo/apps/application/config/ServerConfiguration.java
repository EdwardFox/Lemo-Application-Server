/**
 * File ./src/main/java/de/lemo/apps/application/config/ServerConfiguration.java
 * Lemo-Application-Server for learning analytics.
 * Copyright (C) 2013
 * Leonard Kappe, Andreas Pursian, Sebastian Schwarzrock, Boris Wenzlaff
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/

/**
 * File ServerConfiguration.java
 * Date Feb 14, 2013
 */
package de.lemo.apps.application.config;

import java.io.InputStream;
import java.util.ArrayList;
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
import de.lemo.apps.entities.Course;
import de.lemo.apps.entities.Roles;
import de.lemo.apps.entities.User;

/**
 * @author Leonard Kappe
 */
public enum ServerConfiguration {

	INSTANCE;

	private final Logger logger = Logger.getLogger(this.getClass());
	private String serverName;
	private Map<String, String> dbConfig;
	private String dmsUrl;

	private List<User> userImports;

	public static ServerConfiguration getInstance() {
		return INSTANCE;
	}

	/**
	 * The context path is used to determine the configuration file's name. The first char (a slash by convention) will
	 * be
	 * ignored. Some context paths aren't valid file names (like the root context or those with sub-paths), so tomcat's
	 * naming conventions for .war files will be used.
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
	 * @param contextPath
	 */
	public void loadConfig(final String contextPath) {
		final LemoConfig lemoConfig = this.readConfigFiles(contextPath);
		// TODO set log level from config

		this.serverName = lemoConfig.applicationServer.name;
		this.dmsUrl = lemoConfig.applicationServer.dataManagementServerURL;

		this.dbConfig = Maps.newHashMap();
		for (final PropertyConfig property : lemoConfig.applicationServer.appDbConfig) {
			this.dbConfig.put(property.key, property.value);
		}

		this.userImports = this.createUsers(lemoConfig.applicationServer.users);
	}

	private List<User> createUsers(List<UserConfig> userConfigurations) {
		final List<User> users = Lists.newArrayList();

		for (UserConfig userConfig : userConfigurations) {
			User user = new User(userConfig.fullName, userConfig.name, userConfig.email, userConfig.password);

			if (userConfig.roles != null) {
				for (String role : userConfig.roles) {
					user.getRoles().add(Roles.valueOf(role.toUpperCase()));
				}
			}
			if (userConfig.courses != null) {
				List<Course> courses = new ArrayList<Course>();
				for (Long courseId : userConfig.courses) {
					courses.add(new Course(courseId));
				}
				user.setMyCourses(courses);
			}
			logger.debug("User from config: " + user.getUsername() + " " + user.getRoles() + user.getMyCourseIds());
			users.add(user);
		}

		return users;
	}

	private LemoConfig readConfigFiles(String contextPath) {
		if (contextPath.isEmpty()) {
			// empty means root context, Tomcat convention for root context war files is 'ROOT.war'
			contextPath = "/ROOT";
		}

		// remove leading slash, replace any slashes with hashes like Tomcat does with the war files
		final String warName = contextPath.substring(1).replace('/', '#');

		final Set<String> fileNames = new LinkedHashSet<String>();
		// default, based on war name
		fileNames.add(warName + ".xml");

		int lastHash;
		String warPath = warName;
		while ((lastHash = warPath.lastIndexOf('#')) > 0) {
			// try to read more generic name by removing sub-paths, i.e. /lemo/dms -> /lemo
			// (useful to read the appserver's config file, so only one file is needed)
			warPath = warPath.substring(0, lastHash);
			fileNames.add(warPath + ".xml");
		}

		// eventually try generic lemo.xml for use in local development
		fileNames.add("lemo.xml");

		LemoConfig lemoConfig = null;
		try {
			final Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(LemoConfig.class).createUnmarshaller();
			for (final String fileName : fileNames) {
				final InputStream in = this.getClass().getResourceAsStream("/" + fileName);
				if (in != null) {
					this.logger.info("Using config file: " + fileName);
					lemoConfig = (LemoConfig) jaxbUnmarshaller.unmarshal(in);
				}
			}
		} catch (final JAXBException e) {
			// no way to recover, re-throw at runtime
			throw new RuntimeException(e);
		}
		if (lemoConfig == null) {
			final String files = fileNames.toString();
			throw new RuntimeException(
					"No config file found in the classpath. Files follow tomcat's naming convention for .war files, " +
							"in following order until a valid one is found: "
							+ files.substring(1, files.length() - 1));
		}

		this.logger.info("Config loaded for '" + lemoConfig.applicationServer.name + "'");
		return lemoConfig;
	}

	public String getName() {
		return this.serverName;
	}

	public String getDMSBaseUrl() {
		return this.dmsUrl;
	}

	public Map<String, String> getDbConfig() {
		return this.dbConfig;
	}

	public List<User> getUserImports() {
		return this.userImports;
	}

}
