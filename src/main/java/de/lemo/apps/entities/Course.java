package de.lemo.apps.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.tapestry5.ioc.annotations.Inject;

import de.lemo.apps.restws.entities.CourseObject;

/**
* The course is a domain class which is responsible for .... 
* <p> 
* Based on the basic system structure, next to getter and setter methods there are only a few methods with ....
* Most of the domainspecific logic is 
* 
* 
* Aufgrund der allgemeinen Systemarchitektur sind neben Getter- und Setter-Methoden nur wenige Methoden
* direkt in dieser Domainklasse implementiert. Ein Grossteil der Methoden die Klassenuebergreifende Funktionen implementieren sind im 
* Rahmen des Application-Layer als Teil einer Service Klasse *Worker.java implementiert. 
* <p> 
* ACHTUNG: Important methods are commonly defined at the end of the class and are often marked with a @Transient annotation, to indicate
* that hibernate can ignore these methods 
* <p> 
* Die Klasse wird mittels @Entity Annotation als zu persistierende Klasse gekennzeichnet
* und darauf hin via Hibernate persistiert. Hierfuer erweitert sie die abstrakte Klasse AbstractEntity, welche uebergeordnete Methoden
* und Attribute enthaelt, die Hibernate fuer jede zu persistierende Klasse erwartet (z.B. ID, etc.). Das Datenbank-Mapping kann 
* Hibernate fuer Basisdatentypen (String, Integer, Date, etc.) selbst vornehmen. Erst bei komplexen Datentypen bzw. expl. 
* Assoziationen ist eine Auszeichnung durch zusaetzliche Annotationen an den Getter-Methoden der jeweiligen Attribute notwendig. 
* Beispiele hierfuer sind z.B. @OneToOne fuer eine 1:1 Assoziation und @OneToMany fuer eine 1:n Assoziation.
* <p> 
* @version 1.0
* @author Andreas Pursian
*
*/

@Entity
@Table(name = "course")
public class Course extends AbstractEntity{

	private static final long serialVersionUID = -5156611987477622933L;

	
	private Long courseId;
	private String courseName;
	private String courseDescription;
	private Date lastRequestDate;
	private Date firstRequestDate;
	private Long maxParticipants;
	private Long enroledParticipants;
	private Boolean isFavorite;
	
	@Inject
	public Course() {}
	
	public Course(CourseObject courseObject){
		if(courseObject!=null){
			this.courseName = courseObject.getTitle();
	        this.courseDescription = courseObject.getDescription();
	        if(courseObject.getFirstRequest()!=null)
	        this.firstRequestDate = new java.util.Date((long)courseObject.getFirstRequest()*1000);
	        if(courseObject.getLastRequest()!=null)
	        this.lastRequestDate = new java.util.Date((long)courseObject.getLastRequest()*1000);
	        this.enroledParticipants = courseObject.getParticipants();
	        this.enroledParticipants = 1L;
	        this.courseId=courseObject.getId();
		}
	}
	
	public Course(	final String courseName, final String courseDescription, final Date begin, 
			  		final Date end, final Long maxParticipants, final Long enroledParticipants)
	    {
	        this.courseName = courseName;
	        this.courseDescription = courseDescription;
	        this.lastRequestDate = end;
	        this.firstRequestDate = begin;
	        this.maxParticipants = maxParticipants;
	        this.enroledParticipants = enroledParticipants;
	    }
	
	/**
	 * @return the courseId
	 */
	public Long getCourseId() {
		return courseId;
	}

	/**
	 * @param courseId the courseId to set
	 */
	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	/**
	 * @return the courseName
	 */
	public String getCourseName() {
		return courseName;
	}
	/**
	 * @param courseName the courseName to set
	 */
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	/**
	 * @return the courseDescription
	 */
	public String getCourseDescription() {
		return courseDescription;
	}
	/**
	 * @param courseDescription the courseDescription to set
	 */
	public void setCourseDescription(String courseDescription) {
		this.courseDescription = courseDescription;
	}
	/**
	 * @return the beginDate
	 */
	public Date getLastRequestDate() {
		return lastRequestDate;
	}
	/**
	 * @param beginDate the beginDate to set
	 */
	public void setLastRequestDate(Date lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getFirstRequestDate() {
		return firstRequestDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setFirstRequestDate(Date firstRequestDate) {
		this.firstRequestDate = firstRequestDate;
	}
	/**
	 * @return the maxParticipants
	 */
	public Long getMaxParticipants() {
		return maxParticipants;
	}
	/**
	 * @param maxParticipants the maxParticipants to set
	 */
	public void setMaxParticipants(Long maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
	/**
	 * @return the enroledParticipants
	 */
	public Long getEnroledParticipants() {
		return enroledParticipants;
	}
	/**
	 * @param enroledParticipants the enroledParticipants to set
	 */
	public void setEnroledParticipants(Long enroledParticipants) {
		this.enroledParticipants = enroledParticipants;
	}
	
	/**
	 * @return the isFavorite
	 */
	public Boolean getIsFavorite() {
		return isFavorite;
	}

	/**
	 * @param isFavorite the isFavorite to set
	 */
	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			return (obj instanceof Course && ((Course) obj).getCourseName().equals(courseName));
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return courseName == null ? 0 : courseName.hashCode();
	}
}