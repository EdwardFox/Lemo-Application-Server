/**
 * File ./src/main/java/de/lemo/apps/exceptions/RestServiceCommunicationException.java
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
	 * File RestServiceCommunicationException.java
	 *
	 * Date Feb 14, 2013 
	 *
	 */
package de.lemo.apps.exceptions;

import org.apache.log4j.Logger;

/**
 * @author Andreas Pursian
 */
public class RestServiceCommunicationException extends Exception {

	private final Logger logger = Logger.getLogger(RestServiceCommunicationException.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -4576186737274820490L;

	public RestServiceCommunicationException() {

	}

	public RestServiceCommunicationException(final String serviceName) {
		this.logger.error("Error during REST-Service communication. Service invoked: "+serviceName);
	}
}
