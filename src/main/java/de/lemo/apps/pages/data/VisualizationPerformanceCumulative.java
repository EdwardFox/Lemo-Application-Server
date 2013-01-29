package de.lemo.apps.pages.data;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONLiteral;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.util.EnumSelectModel;
import org.apache.tapestry5.util.EnumValueEncoder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

import se.unbound.tapestry.breadcrumbs.BreadCrumb;
import se.unbound.tapestry.breadcrumbs.BreadCrumbInfo;
import de.lemo.apps.application.AnalysisWorker;
import de.lemo.apps.application.DateWorker;
import de.lemo.apps.application.UserWorker;
import de.lemo.apps.entities.Course;
import de.lemo.apps.integration.CourseDAO;
import de.lemo.apps.pages.data.Explorer;
import de.lemo.apps.restws.client.Analysis;
import de.lemo.apps.restws.client.Initialisation;
import de.lemo.apps.restws.entities.BoxPlot;
import de.lemo.apps.restws.entities.EResourceType;
import de.lemo.apps.restws.entities.ResourceRequestInfo;
import de.lemo.apps.restws.entities.ResultListLongObject;
import de.lemo.apps.restws.entities.ResultListStringObject;
import de.lemo.apps.services.internal.CourseIdSelectModel;
import de.lemo.apps.services.internal.CourseIdValueEncoder;
import de.lemo.apps.services.internal.LongValueEncoder;
import de.lemo.apps.services.internal.jqplot.TextValueDataItem;

@RequiresAuthentication
@BreadCrumb(titleKey = "visualizationTitle")
@Import(library = { "../../js/d3/d3_custom_PerformanceBoxPlot.js" })
public class VisualizationPerformanceCumulative {

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private Logger logger;

    @Inject
    private DateWorker dateWorker;
    
    @Inject
	private AnalysisWorker analysisWorker;
    
    @Inject
    private Initialisation init;

    @Inject
    private CourseIdValueEncoder courseValueEncoder;

    @Inject
    private Analysis analysis;

    @Inject
    private UserWorker userWorker;

    @Inject
    private CourseDAO courseDAO;

    @Inject
    private Locale currentlocale;

    @Inject
    private Messages messages;

    @Inject
    private TypeCoercer coercer;

    @Property
    private BreadCrumbInfo breadCrumb;

    @InjectComponent
    private Zone formZone;

    @Component(id = "customizeForm")
    private Form customizeForm;

    @Property
    @SuppressWarnings("unused")
    private SelectModel courseModel;

    @Property
    @Persist
    private Course course;

    @Property
    @Persist
    private Long courseId;

    @Component(id = "beginDate")
    private DateField beginDateField;

    @Component(id = "endDate")
    private DateField endDateField;

    @Persist
    @Property
    private Date beginDate;

    @Persist
    @Property
    private Date endDate;

    @Property
    @Persist
    Integer resolution;

    @Property
    @Persist
    private List<Course> courses;

    // Value Encoder for activity multi-select component
    @Property(write = false)
    private final ValueEncoder<EResourceType> activityEncoder = new EnumValueEncoder<EResourceType>(coercer,
            EResourceType.class);

    // Select Model for activity multi-select component
    @Property(write = false)
    private final SelectModel activityModel = new EnumSelectModel(EResourceType.class, messages);

    @Property
    @Persist
    private List<EResourceType> selectedActivities;

    @Inject
    @Property
    private LongValueEncoder userIdEncoder, quizEncoder;

    @Property
    @Persist
    private List<Long> userIds, quizIds;

    @Property
    @Persist
    private List<Long> selectedUsers, selectedQuizzes;

    public List<Long> getUsers() {
        List<Long> courses = new ArrayList<Long>();
        courses.add(course.getCourseId());
         List<Long> elements = analysis.computeCourseUsers(courses, beginDate.getTime() / 1000, endDate.getTime() / 1000).getElements();
        logger.info("          ----        "+elements);
         return elements;
    }

    public Object onActivate(Course course) {
        logger.debug("--- Bin im ersten onActivate");
        List<Long> allowedCourses = userWorker.getCurrentUser().getMyCourses();
        if(allowedCourses != null && course != null && course.getCourseId() != null
                && allowedCourses.contains(course.getCourseId())) {
            this.courseId = course.getCourseId();
            this.course = course;
            
            return true;
        } else
            return Explorer.class;
    }

    public Object onActivate() {
        logger.debug("--- Bin im zweiten onActivate");
        return true;
    }

    public Course onPassivate() {
        return course;
    }

    void cleanupRender() {
        customizeForm.clearErrors();
        // Clear the flash-persisted fields to prevent anomalies in onActivate
        // when we hit refresh on page or browser
        // button
        this.courseId = null;
        this.course = null;
        this.selectedUsers = null;
        this.selectedQuizzes = null;
        this.selectedActivities = null;
    }

//    void pageReset() {
//        selectedUsers = null;
//        userIds = getUsers();
//    }

    void onPrepareForRender() {
        List<Course> courses = courseDAO.findAllByOwner(userWorker.getCurrentUser());
        courseModel = new CourseIdSelectModel(courses);
        userIds = getUsers();
        quizIds = new ArrayList<Long>();
        
        List<Long> courseList = new ArrayList<Long>();
		courseList.add(courseId);
        ResultListStringObject quizList = init.getRatedObjects(courseList);
		
		Map<Long,String> quizzesMap = CollectionFactory.newMap();
		List<String> quizzesTitles = new ArrayList<String>();
		
		if(quizList!=null && quizList.getElements()!=null)
		{
			logger.debug(quizList.getElements().toString());
			List<String> quizStringList = quizList.getElements();
			 for(Integer x = 0; x < quizStringList.size(); x=x+3) {
				 Long combinedQuizId = Long.parseLong((quizStringList.get(x)+quizStringList.get(x+1)));
				 quizzesMap.put(combinedQuizId, quizStringList.get(x+2));
				 quizzesTitles.add(quizStringList.get(x+2));
				 quizIds.add(combinedQuizId);
			 }
		
		} else logger.debug("No rated Objetcs found");
    }

    public final ValueEncoder<Course> getCourseValueEncoder() {
        // List<Course> courses =
        // courseDAO.findAllByOwner(userWorker.getCurrentUser());
        return courseValueEncoder.create(Course.class);
    }

    // returns datepicker params
    public JSONLiteral getDatePickerParams() {
        return dateWorker.getDatePickerParams(currentlocale);
    }

    public String getQuestionResult() {
        if(courseId!=null){
        	Long endStamp=0L;
        	Long beginStamp=0L;
        	if(endDate!=null){
        		endStamp = new Long(endDate.getTime()/1000);
        	} //else endtime= 1334447632L;
	        
        	if(beginDate!=null){
        		beginStamp = new Long(beginDate.getTime()/1000);
        	} //else starttime = 1308968800L;
        	
			if (this.resolution == null || this.resolution < 10 )
				this.resolution = 30;
			List<Long> roles = new ArrayList<Long>();
			List<Long> courseList = new ArrayList<Long>();
			courseList.add(courseId);
			
			//calling dm-server
			for (int i=0;i<courseList.size();i++){
				logger.debug("Courses: "+courseList.get(i));
			}
			
//			 List<String> types = null;
//		        if(selectedActivities != null && !selectedActivities.isEmpty()) {
//		            types = new ArrayList<String>();
//		            for(EResourceType resourceType : selectedActivities) {
//		                types.add(resourceType.name().toLowerCase());
//		            }
//		        }
//        	
		        
		        List<Long> quizzesList = new ArrayList<Long>();
				//quizzesList.add(11114282L);
				//quizzesList.add(11114861L);
		    	
		    	ResultListStringObject quizList = init.getRatedObjects(courseList);
				
				Map<Long,String> quizzesMap = CollectionFactory.newMap();
				List<String> quizzesTitles = new ArrayList<String>();
				
				if(quizList!=null && quizList.getElements()!=null)
				{
					logger.debug(quizList.getElements().toString());
					List<String> quizStringList = quizList.getElements();
					 for(Integer x = 0; x < quizStringList.size(); x=x+3) {
						 Long combinedQuizId = Long.parseLong((quizStringList.get(x)+quizStringList.get(x+1)));
						 quizzesMap.put(combinedQuizId, quizStringList.get(x+2));
						 quizzesTitles.add(quizStringList.get(x+2));
					 }
				
				} else logger.debug("No rated Objetcs found");
					
				if(selectedQuizzes != null){
					quizzesList = selectedQuizzes;
				} else if(quizzesMap!=null && quizzesMap.keySet()!=null){
					quizzesList = new ArrayList<Long>();
					quizzesList.addAll(quizzesMap.keySet());
				}    
		        
			
			logger.debug("Starttime: "+beginStamp+ " Endtime: "+endStamp+ " Resolution: "+resolution+" QuizzesAmount:"+quizzesList.size());
		 
			
			
			
			String result = analysis.computePerformanceBoxplot(courseList, selectedUsers, quizzesList, beginStamp, endStamp);
			
			JSONArray graphParentArray = new JSONArray();
			
			
			ObjectMapper mapper = new ObjectMapper();
			List<BoxPlot> resultList;
			BoxPlot singleResult;
			String resultListString = "";
			JsonNode jsonObj;
			try {
				jsonObj = mapper.readTree(result);
				JsonNode elementArray = jsonObj.get("elements");
	            if (elementArray != null ) {
		            	if(elementArray.isArray()) {
		            
		           	 	
		            	resultList = mapper.readValue(elementArray.toString(), new TypeReference<List<BoxPlot>>() { });
		            	
		            	logger.debug("Entries Size:"+jsonObj.get("elements").size()+" Values:"+jsonObj.get("elements").toString());
		            	
		            	logger.debug("Entries parsed Size: "+resultList.size()+" Values:"+resultList.toString());
		            	
		            	for(Integer w = 0; w < resultList.size(); w++) {
		            		Long quizID = Long.parseLong(resultList.get(w).getName());
		            		resultList.get(w).setName(quizzesMap.get(quizID));
		            		//resultList.get(w).setName(quizzesTitles.get(w));
		            	}
		            	
		            	resultListString = mapper.writeValueAsString(resultList);
		            	
		            	logger.debug("Entries JSON Output: "+resultListString);
		            } else {
		            	singleResult = mapper.readValue(elementArray.toString(), new TypeReference<BoxPlot>() { });
		            	
		            	logger.debug("Entries: "+jsonObj.get("elements").toString());
		            	
		            	logger.debug("Entries parsed: "+singleResult.toString());
		            	
		            	
		            	Long quizID = Long.parseLong(singleResult.getName());
		            	singleResult.setName(quizzesMap.get(quizID));
		            	//resultList.get(w).setName(quizzesTitles.get(w));
		            	
		            	List<BoxPlot> tmpResultList = new ArrayList<BoxPlot>();
		            	tmpResultList.add(singleResult);
		            	
		            	resultListString = mapper.writeValueAsString(tmpResultList);
		            	
		            }
	            }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			logger.debug("Cumulative result: "+result);
			return resultListString;

        }
        return "";
    }

    void setupRender() {
        logger.debug(" ----- Bin in Setup Render");

        ArrayList<Long> courseList = new ArrayList<Long>();
        courseList.add(course.getCourseId());
        
        
        if(this.endDate == null){
            this.endDate = course.getLastRequestDate();
        } else {
       	 	this.selectedUsers = null;
       	 	userIds = getUsers();
       }
        if(this.beginDate == null){
            this.beginDate = course.getFirstRequestDate();
        } else {
       	 	this.selectedUsers = null;
       	 	userIds = getUsers();
        }
        Calendar beginCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        beginCal.setTime(beginDate);
        endCal.setTime(endDate);
        this.resolution = dateWorker.daysBetween(beginDate, endDate);

        
        
//
//        Calendar beginCal = Calendar.getInstance();
//        Calendar endCal = Calendar.getInstance();
//        beginCal.setTime(beginDate);
//        endCal.setTime(endDate);
//        this.resolution = dateWorker.daysBetween(beginDate, endDate);
//        logger.debug("SetupRender End --- BeginDate:" + beginDate + " EndDate: " + endDate + " Res: " + resolution);
    }

    @AfterRender
    public void afterRender() {
        javaScriptSupport.addScript("");
    }

    void onPrepareFromCustomizeForm() {
        this.course = courseDAO.getCourseByDMSId(courseId);
    }

    void onSuccessFromCustomizeForm() {
        logger.debug("   ---  onSuccessFromCustomizeForm ");
        logger.debug("Selected activities: " + selectedActivities);
        logger.debug("Selected users: " + selectedUsers);
    }

    public String getLocalizedDate(Date inputDate) {
        SimpleDateFormat df_date = new SimpleDateFormat("MMM dd, yyyy", currentlocale);
        return df_date.format(inputDate);
    }

    public String getFirstRequestDate() {
        return getLocalizedDate(this.beginDate);//.course.getFirstRequestDate());
    }

    public String getLastRequestDate() {
        return getLocalizedDate(this.endDate);//.course.getLastRequestDate());
    }
}