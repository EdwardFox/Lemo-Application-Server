/**
 * File ./de/lemo/apps/restws/proxies/questions/QCourseUsers.java
 * Date 2013-01-29
 * Project Lemo Learning Analytics
 * Copyright TODO (INSERT COPYRIGHT)
 */

package de.lemo.apps.restws.proxies.questions;

import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import de.lemo.apps.restws.entities.ResultListLongObject;
import de.lemo.apps.restws.proxies.questions.parameters.MetaParam;

public interface QCourseUsers {

	@POST
	@Path("activecourseusers")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultListLongObject compute(
			@FormParam(MetaParam.COURSE_IDS) List<Long> courseIds,
			@FormParam(MetaParam.START_TIME) Long startTime,
			@FormParam(MetaParam.END_TIME) Long endTime);

}
