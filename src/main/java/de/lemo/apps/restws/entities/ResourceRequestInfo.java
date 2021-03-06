/**
 * File ./src/main/java/de/lemo/apps/restws/entities/ResourceRequestInfo.java
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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Information for an resource object
 *
 */
@XmlRootElement
public class ResourceRequestInfo {

	private Long id;
	private String resourcetype;
	private Long requests;
	private Long users;
	private String title;
	private Long resolutionSlot;

	public Long getUsers() {
		return this.users;
	}

	public void setUsers(final Long users) {
		this.users = users;
	}

	public Long getResolutionSlot() {
		return this.resolutionSlot;
	}

	public void setResolutionSlot(final Long resolutionSlot) {
		this.resolutionSlot = resolutionSlot;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getResourcetype() {
		return this.resourcetype;
	}

	public void setResourcetype(final String resourcetype) {
		this.resourcetype = resourcetype;
	}

	public Long getRequests() {
		return this.requests;
	}

	public void setRequests(final Long requests) {
		this.requests = requests;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public ResourceRequestInfo() {
	}

	public void incRequests() {
		this.requests++;
	}

	public ResourceRequestInfo(final Long id, final String resourceType, final Long requests, final String title, final Long resolutionSlot) {
		this.id = id;
		this.resourcetype = resourceType;
		this.requests = requests;
		this.title = title;
		this.resolutionSlot = resolutionSlot;
	}

}