/**
 * File ./src/main/java/de/lemo/apps/restws/entities/ResultListCourseObject.java
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

package de.lemo.apps.restws.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Result list with course objects
 */
@XmlRootElement
public class ResultListCourseObject {

	private List<CourseObject> elements;

	public ResultListCourseObject() {

	}

	public ResultListCourseObject(final List<CourseObject> elements) {
		this.elements = elements;
	}

	@XmlElement
	public List<CourseObject> getElements() {
		return this.elements;
	}
	
	public void setElements(final List<CourseObject> elements) {
		this.elements = elements;
	}

}
