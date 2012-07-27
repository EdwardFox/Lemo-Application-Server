/**
 * 
 */
package de.lemo.apps.application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.slf4j.Logger;

import de.lemo.apps.entities.Course;
import de.lemo.apps.restws.client.Analysis;
import de.lemo.apps.restws.entities.EResourceType;
import de.lemo.apps.restws.entities.ResourceRequestInfo;
import de.lemo.apps.restws.entities.ResultListLongObject;
import de.lemo.apps.restws.entities.ResultListRRITypes;
import de.lemo.apps.restws.entities.ResultListResourceRequestInfo;
import de.lemo.apps.services.internal.jqplot.XYDateDataItem;

/**
 * @author johndoe
 *
 */
public class AnalysisWorkerImpl implements AnalysisWorker{
	
	@Inject
    private Logger logger;
	
	@Inject
	private DateWorker dateWorker;
	
	@Inject
	private Analysis analysis;
	
	public List<ResourceRequestInfo> usageAnalysisExtended(Course course, Date beginDate, Date endDate, List<EResourceType> resourceTypes){
        
    	if(course!=null && course.getId()!=null){
        	Long endStamp=0L;
        	Long beginStamp=0L;
        	List<String> resourceTypesNames = null;
        	
        	if(endDate!=null){
        		
        		endStamp = new Long(endDate.getTime()/1000);
        	} 
	        
        	if(beginDate!=null){
        		
        		beginStamp = new Long(beginDate.getTime()/1000);
        	} 
        	
        	if(resourceTypes!=null && resourceTypes.size() >=1){
        		resourceTypesNames = new ArrayList<String>();
        		for(int i = 0; i<resourceTypes.size();i++){
        			resourceTypesNames.add(resourceTypes.get(i).toString());
        			logger.debug("Resource Typ: "+resourceTypes.get(i).toString());
        		}
        	}

			List<Long> roles = new ArrayList<Long>();
			List<Long> courses = new ArrayList<Long>();
			courses.add(course.getCourseId());
			
			//calling dm-server
			for (int i=0;i<courses.size();i++){
				logger.debug("Courses: "+courses.get(i));
			}
			logger.debug("Starttime: "+beginStamp+ " Endtime: "+endStamp+ " ");
			
			logger.debug("Starting Extended Analysis");
			ResultListResourceRequestInfo results = analysis.computeQ1Extended(courses, beginStamp, endStamp, resourceTypesNames);
			logger.debug("Extended Analysis: "+ results);
			if(results!= null && results.getResourceRequestInfos()!=null && results.getResourceRequestInfos().size() > 0){
		        for(int i=0 ;i<results.getResourceRequestInfos().size();i++){
		        	ResourceRequestInfo res = results.getResourceRequestInfos().get(i);
		        	//logger.debug("ResourceRequest"+ res.getTitle()+" ----- "+ res.getResourcetype());
		        }
				return results.getResourceRequestInfos();
			}
    		} else logger.debug("Extended Analysis Result is null!");
		return new ArrayList<ResourceRequestInfo>();
	}
	
	
	public ResultListRRITypes usageAnalysisExtendedDetails(Course course, Date beginDate, Date endDate, Integer resolution, List<EResourceType> resourceTypes){
        
    	if(course!=null && course.getId()!=null){
        	Long endStamp=0L;
        	Long beginStamp=0L;
        	List<String> resourceTypesNames = null;
        	
        	if(endDate!=null){
        		
        		endStamp = new Long(endDate.getTime()/1000);
        	} 
	        
        	if(beginDate!=null){
        		
        		beginStamp = new Long(beginDate.getTime()/1000);
        	} 
        	
        	if(resourceTypes!=null && resourceTypes.size() >=1){
        		resourceTypesNames = new ArrayList<String>();
        		for(int i = 0; i<resourceTypes.size();i++){
        			resourceTypesNames.add(resourceTypes.get(i).toString());
        		}
        	}

			List<Long> roles = new ArrayList<Long>();
			List<Long> courses = new ArrayList<Long>();
			courses.add(course.getCourseId());
			
			//calling dm-server
			for (int i=0;i<courses.size();i++){
				logger.debug("Courses: "+courses.get(i));
			}
			logger.debug("Starttime: "+beginStamp+ " Endtime: "+endStamp+ " ");
			
			logger.debug("Starting Extended Analysis");
			ResultListRRITypes results = analysis.computeQ1ExtendedDetails(courses, beginStamp, endStamp, resolution.longValue(), resourceTypesNames);
			logger.debug("Extended Analysis: "+ results);
			if(results!= null ){
				if(results.getAssignmentRRI()!=null){
					List<ResourceRequestInfo> ass = results.getAssignmentRRI();
					if(ass.size() > 0){
						for(int i=0 ;i<ass.size();i++){
							ResourceRequestInfo res = ass.get(i);
							logger.debug("ASS ResourceRequest "+ res.getId() + " ----- "+ res.getTitle()+" ----- "+ res.getResourcetype()+ " ------- "+res.getRequests()+" ----- " +res.getResolutionSlot());
						}
					}
				}
				
				if(results.getCoursesRRI()!=null){
					List<ResourceRequestInfo> cou = results.getCoursesRRI();
					if(cou.size() > 0){
						for(int i=0 ;i<cou.size();i++){
							ResourceRequestInfo res = cou.get(i);
							logger.debug("Cou ResourceRequest "+ res.getId() + " ----- "+ res.getTitle()+" ----- "+ res.getResourcetype()+ " ------- "+res.getRequests()+" ----- " +res.getResolutionSlot());
						}
					}
				}
				
				if(results.getResourcesRRI()!=null){
					List<ResourceRequestInfo> ress = results.getResourcesRRI();
					if(ress.size() > 0){
						for(int i=0 ;i<ress.size();i++){
							ResourceRequestInfo res = ress.get(i);
							logger.debug("Res ResourceRequest "+ res.getId() + " ----- "+ res.getTitle()+" ----- "+ res.getResourcetype()+ " ------- "+res.getRequests()+" ----- " +res.getResolutionSlot());
						}
					}
				}
				
				if(results.getResourcesRRI()!=null){
					List<ResourceRequestInfo> ress = results.getForumRRI();
					if(ress.size() > 0){
						for(int i=0 ;i<ress.size();i++){
							ResourceRequestInfo res = ress.get(i);
							logger.debug("Forum ResourceRequest "+ res.getId() + " ----- "+ res.getTitle()+" ----- "+ res.getResourcetype()+ " ------- "+res.getRequests()+" ----- " +res.getResolutionSlot());
						}
					}
				}
				
				return results;
			}
    	
    	} else logger.debug("Extended Analysis Result is null!");
			
		return new ResultListRRITypes();
	}
	
	
	
	public List usageAnalysis(Course course, Date endDate, final int dateRange, Integer dateMultiplier){
		
		Date beginDate = endDate;
		
		if(endDate!=null && dateMultiplier !=null){
        	Calendar cal = Calendar.getInstance();
        	logger.debug("EndDate : "+endDate);
    		logger.debug("BeginDate before computation: "+beginDate);
        	cal.setTime(beginDate);
    		
    		cal.add(dateRange,dateMultiplier);
    		beginDate = cal.getTime();
    		logger.debug("BeginDate after computation: "+beginDate);
        } 
		return usageAnalysis(course, beginDate, endDate);
	}
		
	
	public List usageAnalysis(Course course, Date beginDate, Date endDate){
		
		List<List<XYDateDataItem>> dataList = CollectionFactory.newList();
        List<XYDateDataItem> list1 = CollectionFactory.newList();
        Integer resolution = 0;
        
        if(beginDate!=null && endDate!=null)
        	resolution = dateWorker.daysBetween(beginDate, endDate);
        
    	if(course!=null && course.getId()!=null){
        	Long endStamp=0L;
        	Long beginStamp=0L;
        	
        	if(endDate!=null){
        		
        		endStamp = new Long(endDate.getTime()/1000);
        	} 
	        
        	if(beginDate!=null){
        		
        		beginStamp = new Long(beginDate.getTime()/1000);
        	} 
        	

			List<Long> roles = new ArrayList<Long>();
			List<Long> courses = new ArrayList<Long>();
			courses.add(course.getCourseId());
			
			//calling dm-server
			for (int i=0;i<courses.size();i++){
				logger.debug("Courses: "+courses.get(i));
			}
			logger.debug("Starttime: "+beginStamp+ " Endtime: "+endStamp+ " Resolution: "+resolution);
			ResultListLongObject results = analysis.computeQ1(courses, roles, beginStamp, endStamp, resolution);
			
			Calendar beginCal = Calendar.getInstance();
			beginCal.setTime(beginDate);
			
			logger.debug("BeginDate: "+beginDate);
			
			//checking if result size matches resolution 
			if(results!= null && results.getElements()!=null && results.getElements().size() == resolution)
	        for(int i=0 ;i<resolution;i++){
	        	
	        	beginCal.add(Calendar.DAY_OF_MONTH, 1);
	        	list1.add(new XYDateDataItem(beginCal.getTime() , results.getElements().get(i)));
	        }
    	}
        dataList.add(list1);
        return dataList;
	}

}