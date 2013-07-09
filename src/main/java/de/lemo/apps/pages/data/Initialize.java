/**
 * File ./src/main/java/de/lemo/apps/pages/data/Initialize.java Lemo-Application-Server for learning analytics. Copyright
 * (C) 2013 Leonard Kappe, Andreas Pursian, Sebastian Schwarzrock, Boris Wenzlaff
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 **/

/**
 * File Initialize.java
 * 
 * Date Feb 14, 2013
 * 
 */
package de.lemo.apps.pages.data;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.hibernate.Session;
import org.slf4j.Logger;

import de.lemo.apps.entities.Roles;
import de.lemo.apps.entities.User;
import de.lemo.apps.exceptions.RestServiceCommunicationException;
import de.lemo.apps.integration.CourseDAO;
import de.lemo.apps.integration.UserDAO;
import de.lemo.apps.pages.admin.DashboardAdmin;
import de.lemo.apps.restws.client.Initialisation;
import de.lemo.apps.restws.entities.CourseObject;

@RequiresAuthentication
public class Initialize {

    /**
     * Lock objects mapped by user names because we can't use user name/id directly as lock (String interns and primitive's
     * wrapper caching is VM dependent).
     */
    private static ConcurrentHashMap<String, Object> userlocks = new ConcurrentHashMap<String, Object>();

    @Inject
    private Initialisation init;

    @Inject
    private CourseDAO courseDAO;

    @Inject
    private HttpServletRequest request;

    @Inject
    UserDAO ud;

    @Inject
    ComponentResources compRessources;

    @Component(parameters = { "event=progress", "id=literal:progress" })
    private EventLink progress;

    @Inject
    Logger logger;

    @Property
    @Persist
    private long percentage;

    @Property
    @Persist
    private double multi;

    public String getUserName() {

        return this.request.getRemoteUser();

    }

    void onPrepareForRender() {

        this.percentage = 0;
    }

    void cleanupRender() {
        this.percentage = 0;
        this.multi = 0;
    }

    public Object onProgressiveDisplay() {
        String userName = this.getUserName();
        userlocks.putIfAbsent(userName, new Object());
        Object lock = userlocks.get(userName);

        synchronized(lock) {
            // any hibernate queries must be within this block to avoid concurrent update errors
            User user = this.ud.getUser(userName);
            final List<Long> userCourses = user.getMyCourseIds();

            if(userCourses != null && userCourses.size() > 0) {
                this.multi = 100 / userCourses.size();

                if(userCourses != null) {
                    for(int i = 0; i < userCourses.size(); i++) {
                        this.percentage = Math.round((i + 1) * multi);
                        logger.debug("Looking if course ID:" + userCourses.get(i) + " needs update.");
                        if(this.courseDAO.courseNeedsUpdate(userCourses.get(i))) {
                            CourseObject updateObject = null;
                            try {
                                updateObject = this.init.getCourseDetails(userCourses.get(i));
                            } catch (RestServiceCommunicationException e) {
                                logger.error(e.getMessage());
                            }
                            if(updateObject != null) {
                                logger.debug("ID of updated object is: " + updateObject.getId()
                                        + "  ----  " + updateObject.getTitle() + "  ----  "
                                        + updateObject.getDescription());
                                this.courseDAO.update(updateObject);
                            }
                        }
                    }
                }
            }
            // don't keep locks forever
            userlocks.remove(userName);
            return user.getRoles().contains(Roles.ADMIN) ? DashboardAdmin.class : Dashboard.class;
        }
    }

    Object onProgress() {
        JSONObject progress = new JSONObject();
        logger.info("Prgress Request, progress:" + this.percentage);
        progress.append("progress", String.valueOf(this.percentage) + "%");
        return progress;
    }

    public String getProgressEventURI() {
        return compRessources.createEventLink("Progress").toAbsoluteURI();
    }

    public String getStatusBar() {
        return String.valueOf(this.percentage) + "%";
    }

}
