/**
 * File ./src/main/java/de/lemo/apps/application/VisualisationHelperWorker.java
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
	 * File VisualisationHelperWorker.java
	 *
	 * Date Feb 14, 2013 
	 *
	 */
package de.lemo.apps.application;

import java.util.List;
import de.lemo.apps.entities.GenderEnum;
import de.lemo.apps.restws.entities.EResourceType;


public interface VisualisationHelperWorker {
	
	List<Long> getGenderIds(List<GenderEnum> genderList);
	
	List<String> getActivityIds(List<EResourceType> activities);

}
