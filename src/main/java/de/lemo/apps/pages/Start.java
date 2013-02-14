package de.lemo.apps.pages;

import java.util.Date;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SymbolConstants;
import org.slf4j.Logger;
import org.tynamo.security.services.PageService;
import org.tynamo.security.services.SecurityService;
import de.lemo.apps.exceptions.RestServiceCommunicationException;
import de.lemo.apps.restws.client.Initialisation;
import de.lemo.apps.restws.entities.ResultListLongObject;

/**
 * Start page of application test.
 */
public class Start {

	@Property
	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	private String tapestryVersion;

	@Inject
	@Path("../images/Nutzungsanalyse.png")
	@Property
	private Asset carussel_one;

	@Inject
	@Path("../images/Pfadvisualisierung.png")
	@Property
	private Asset carussel_two;

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;
	
	@Inject
	private Initialisation init;

	@Inject
	private SecurityService securityService;

	@SuppressWarnings("deprecation")
	@Inject
	private PageService pageService;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String username;

	@Property
	private String password;

	@AfterRender
	public void afterRender() {
		this.javaScriptSupport.addScript(
					"$('.carousel').carousel({ " +
							"interval: 8000" +
							"})"
				);
	}

	@Component
	private Form loginForm;

	@Persist
	@Property
	private int clickCount;

	@Inject
	private AlertManager alertManager;

	public Date getCurrentTime() {
		return new Date();
	}

	public Object onSubmitFromLoginForm() {

		try {
			final Subject currentUser = this.securityService.getSubject();

			if (currentUser == null) {
				throw new IllegalStateException("Error during login. Can't obtain user from security service.");
			}

			final UsernamePasswordToken token = new UsernamePasswordToken(this.username, this.password);
			this.logger.info("Prepare Logintoken. Username: " + this.username);

			ResultListLongObject result = init.identifyUserName(this.username);
			
			if (result  != null && result.getElements()!=null && result.getElements().size() > 0) {
				
				logger.debug("Corresponding LeMo user ID : "+result.getElements().get(result.getElements().size()-1));
				
			} else logger.debug("No matching user found");
				
			
			currentUser.login(token);

		} catch (final AuthenticationException ex) {
			this.logger.info("Login unsuccessful.");
			this.loginForm.recordError(this.messages.get("error.login"));
			this.alertManager.info("Login or password not correct.");
			return null;
		} catch (RestServiceCommunicationException e) {
			this.logger.info("Login unsuccessful.");
			this.loginForm.recordError(this.messages.get("error.login"));
			this.alertManager.info("Login server not available at the moment. Please try again later.");
			return null;
		}

		return this.pageService.getSuccessPage();
	}

}
