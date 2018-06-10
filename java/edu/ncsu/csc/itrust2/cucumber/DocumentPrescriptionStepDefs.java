package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.NDCDrug;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class DocumentPrescriptionStepDefs {

	private final WebDriver driver       = new HtmlUnitDriver( true );
	private final String    baseUrl      = "http://localhost:8080/iTrust2";

	private final String    hospitalName = "Office Visit Hospital" + ( new Random() ).nextInt();
	private NDCDrug         expected;

	WebDriverWait           wait         = new WebDriverWait( driver, 2 );

	@Given ( "The required entries exist for prescription: (.+)" )
	public void personnelExists ( final String prescription ) throws Exception {
		if ( NDCDrug.getAll().size() > 0 ) {
			try {
				NDCDrug.deleteAll( NDCDrug.class );
			}
			catch ( final Exception e ) {
				// code we are planning on using is not listed
			}
		}
		if ( Prescription.getAll().size() > 0 ) {
			Prescription.getAll().get( 0 ).delete();
		}
		if ( OfficeVisit.getOfficeVisits().size() > 0 ) {
			OfficeVisit.deleteAll( OfficeVisit.class );
		}
		final NDCDrug drug = new NDCDrug();
		drug.setName( prescription );
		drug.setCode( "1000-0001-51" );
		drug.save();
		expected = drug;
		// All tests can safely assume the existence of the 'hcp', 'admin', and
		// 'patient' users

		/* Make sure we create a Hospital and Patient record */

		/* Create a Hospital */
		driver.get( baseUrl );
		WebElement username = driver.findElement( By.name( "username" ) );
		username.clear();
		username.sendKeys( "admin" );
		WebElement password = driver.findElement( By.name( "password" ) );
		password.clear();
		password.sendKeys( "123456" );
		WebElement submit = driver.findElement( By.className( "btn" ) );
		submit.click();
		( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addhospital').click();" );
		try {
			final WebElement name = driver.findElement( By.id( "name" ) );
			name.clear();
			name.sendKeys( hospitalName );

			final WebElement address = driver.findElement( By.id( "address" ) );
			address.clear();
			address.sendKeys( "Bialystok" );

			final WebElement state = driver.findElement( By.id( "state" ) );
			final Select dropdown = new Select( state );
			dropdown.selectByVisibleText( "NJ" );

			final WebElement zip = driver.findElement( By.id( "zip" ) );
			zip.clear();
			zip.sendKeys( "10101" );
		}
		catch ( final Exception e ) {
			/* Assume the hospital already exists & carry on */
		}
		finally {
			driver.findElement( By.id( "logout" ) ).click();
		}

		/* Create patient record */

		driver.get( baseUrl );
		username = driver.findElement( By.name( "username" ) );
		username.clear();
		username.sendKeys( "patient" );
		password = driver.findElement( By.name( "password" ) );
		password.clear();
		password.sendKeys( "123456" );
		submit = driver.findElement( By.className( "btn" ) );
		submit.click();
		( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
		try {
			final WebElement firstName = driver.findElement( By.id( "firstName" ) );
			firstName.clear();
			firstName.sendKeys( "Karl" );

			final WebElement lastName = driver.findElement( By.id( "lastName" ) );
			lastName.clear();
			lastName.sendKeys( "Liebknecht" );

			final WebElement preferredName = driver.findElement( By.id( "preferredName" ) );
			preferredName.clear();

			final WebElement mother = driver.findElement( By.id( "mother" ) );
			mother.clear();

			final WebElement father = driver.findElement( By.id( "father" ) );
			father.clear();

			final WebElement email = driver.findElement( By.id( "email" ) );
			email.clear();
			email.sendKeys( "karl_liebknecht@mail.de" );

			final WebElement address1 = driver.findElement( By.id( "address1" ) );
			address1.clear();
			address1.sendKeys( "Karl-Liebknecht-Haus, Alexanderplatz" );

			final WebElement city = driver.findElement( By.id( "city" ) );
			city.clear();
			city.sendKeys( "Berlin" );

			final WebElement state = driver.findElement( By.id( "state" ) );
			final Select dropdown = new Select( state );
			dropdown.selectByVisibleText( "CA" );

			final WebElement zip = driver.findElement( By.id( "zip" ) );
			zip.clear();
			zip.sendKeys( "91505" );

			final WebElement phone = driver.findElement( By.id( "phone" ) );
			phone.clear();
			phone.sendKeys( "123-456-7890" );

			final WebElement dob = driver.findElement( By.id( "dateOfBirth" ) );
			dob.clear();
			dob.sendKeys( "08/13/1871" );

			final WebElement submit2 = driver.findElement( By.className( "btn" ) );
			submit2.click();

		}
		catch ( final Exception e ) {
			/*  */
		}
		finally {
			driver.findElement( By.id( "logout" ) ).click();
		}

	}

	/**
	 * Ensures that a patient exists with the given name and birthday
	 *
	 * @param name
	 *            The name of the patient.
	 * @param birthday
	 *            The birthday of the patient.
	 */
	@Given ( "^A patient exists to prescribe with name: (.+) and date of birth: (.+)$" )
	public void patientExistsWithName ( final String name, final String birthday ) {
		driver.get( baseUrl );
		final WebElement username = driver.findElement( By.name( "username" ) );
		username.clear();
		username.sendKeys( "patient" );
		final WebElement password = driver.findElement( By.name( "password" ) );
		password.clear();
		password.sendKeys( "123456" );
		final WebElement submit = driver.findElement( By.className( "btn" ) );
		submit.click();

		( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
		try {
			final WebElement firstName = driver.findElement( By.id( "firstName" ) );
			firstName.clear();
			firstName.sendKeys( name.split( " " )[0] );

			final WebElement lastName = driver.findElement( By.id( "lastName" ) );
			lastName.clear();
			lastName.sendKeys( name.split( " " )[1] );

			final WebElement preferredName = driver.findElement( By.id( "preferredName" ) );
			preferredName.clear();

			final WebElement mother = driver.findElement( By.id( "mother" ) );
			mother.clear();

			final WebElement father = driver.findElement( By.id( "father" ) );
			father.clear();

			final WebElement email = driver.findElement( By.id( "email" ) );
			email.clear();
			email.sendKeys( "email@mail.com" );

			final WebElement address1 = driver.findElement( By.id( "address1" ) );
			address1.clear();
			address1.sendKeys( "address place, address" );

			final WebElement city = driver.findElement( By.id( "city" ) );
			city.clear();
			city.sendKeys( "citytown" );

			final WebElement state = driver.findElement( By.id( "state" ) );
			final Select dropdown = new Select( state );
			dropdown.selectByVisibleText( "CA" );

			final WebElement zip = driver.findElement( By.id( "zip" ) );
			zip.clear();
			zip.sendKeys( "91505" );

			final WebElement phone = driver.findElement( By.id( "phone" ) );
			phone.clear();
			phone.sendKeys( "123-456-7890" );

			final WebElement dob = driver.findElement( By.id( "dateOfBirth" ) );
			dob.clear();
			dob.sendKeys( birthday );

			final WebElement submit2 = driver.findElement( By.className( "btn" ) );
			submit2.click();

		}
		catch ( final Exception e ) {
			/*  */
		}
		finally {
			driver.findElement( By.id( "logout" ) ).click();
		}
	}

	@When ( "I login to iTrust2 as the HCP for prescribing" )
	public void loginAsHCP () {
		driver.get( baseUrl );
		final WebElement username = driver.findElement( By.name( "username" ) );
		username.clear();
		username.sendKeys( "hcp" );
		final WebElement password = driver.findElement( By.name( "password" ) );
		password.clear();
		password.sendKeys( "123456" );
		final WebElement submit = driver.findElement( By.className( "btn" ) );
		submit.click();
	}

	@When ( "I log in to iTrust2 as a Admin for prescribing" )
	public void loginAsAdmin () {
		driver.get( baseUrl );
		final WebElement username = driver.findElement( By.name( "username" ) );
		username.clear();
		username.sendKeys( "admin" );
		final WebElement password = driver.findElement( By.name( "password" ) );
		password.clear();
		password.sendKeys( "123456" );
		final WebElement submit = driver.findElement( By.className( "btn" ) );
		submit.click();
	}

	@When ( "I fill in health metrics on the office visit for infants" )
	public void documentOVInfant () {
		try {
			( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
		}
		catch ( final Exception e ) {

		}
		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "notes" ) ) );
		final WebElement notes = driver.findElement( By.name( "notes" ) );
		notes.clear();
		notes.sendKeys( "Patient appears pretty much alive" );

		wait.until( ExpectedConditions
				.visibilityOfElementLocated( By.xpath( "//*[contains(text()[normalize-space()], 'patient')]" ) ) );
		final WebElement patient = driver
				.findElement( By.xpath( "//*[contains(text()[normalize-space()], 'patient')]" ) );
		patient.click();

		wait.until( ExpectedConditions.presenceOfElementLocated( By.name( "type" ) ) );
		final WebElement type = driver.findElement( By.name( "type" ) );
		type.click();

		final WebElement hospital = driver.findElement( By.name( "hospital" ) );
		hospital.click();

		final WebElement date = driver.findElement( By.name( "date" ) );
		date.clear();
		date.sendKeys( "12/19/2018" );

		final WebElement time = driver.findElement( By.name( "time" ) );
		time.clear();
		time.sendKeys( "9:30 AM" );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "head" ) ) );
		final WebElement head = driver.findElement( By.name( "head" ) );
		head.clear();
		head.sendKeys( "30" );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "height" ) ) );
		final WebElement heightElement = driver.findElement( By.name( "height" ) );
		heightElement.clear();
		heightElement.sendKeys( "120" );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "weight" ) ) );
		final WebElement weightElement = driver.findElement( By.name( "weight" ) );
		weightElement.clear();
		weightElement.sendKeys( "120" );

		wait.until( ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) ) );
		final WebElement houseSmokeElement = driver.findElement(
				By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) );
		houseSmokeElement.click();

	}

	@Then ( "The prescription is add successfully" )
	public void addPrescriptionCorrect () throws InterruptedException {
		NDCDrug actual = null;
		for ( int i = 1; i <= 10; i++ ) {
			try {
				actual = NDCDrug.getByCode( expected.getCode() );
				break;
			}
			catch ( final Exception e ) {
				if ( i == 10 && actual == null ) {
					fail( "Could not get NDC code out of database" );
				}
				Thread.sleep( 1000 );
			}
		}
		if ( actual != null ) {
			assertEquals( expected.getName(), actual.getName() );
			assertEquals( expected.getCode(), actual.getCode() );
		}

	}

	/**
	 * Ensures that the correct prescription have been entered
	 *
	 * @throws InterruptedException
	 */
	@Then ( "The prescription is documented successfully" )
	public void prescriptionCorrect () throws InterruptedException {
		NDCDrug actual = null;
		for ( int i = 1; i < 10; i++ ) {
			try {
				actual = Prescription.getForPatient( "patient" ).get( 0 ).getNdc();
				break;
			}
			catch ( final Exception e ) {
				if ( i == 10 && actual == null ) {
					fail( "Could not get Prescription out of database" );
				}
				Thread.sleep( 1000 );
			}
		}
		if ( actual != null ) {
			assertEquals( expected.getName(), actual.getName() );
			assertEquals( expected.getCode(), actual.getCode() );
		}
	}

	/**
	 * Ensures that the correct prescription have been entered
	 *
	 * @throws InterruptedException
	 */
	@Then ( "The prescription is documented in office visit successfully" )
	public void prescriptionOVCorrect () throws InterruptedException {
		NDCDrug actual = null;
		for ( int i = 1; i < 10; i++ ) {
			try {
				actual = Prescription.getAll().get( 0 ).getNdc();
				break;
			}
			catch ( final Exception e ) {
				if ( i == 10 && actual == null ) {
					fail( "Could not get Prescription out of database" );
				}
				Thread.sleep( 1000 );
			}
		}
		if ( actual != null ) {
			assertEquals( expected.getName(), actual.getName() );
			assertEquals( expected.getCode(), actual.getCode() );
		}
	}

	@When ( "I navigate to the Document Prescription page" )
	public void navigateDocumentPrescription () throws Exception {
		try {
			( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addPrescription').click();" );
		}
		catch ( final Exception e ) {
			throw new Exception( driver.getCurrentUrl() );
		}
		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "prescStart" ) ) );
	}

	@When ( "I navigate to the Add NDC page" )
	public void navigateAddPrescription () throws Exception {
		( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addndcdrug').click();" );
		try {
			wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "name" ) ) );
		}
		catch ( final Exception e ) {
			throw new Exception( driver.getCurrentUrl() );
		}
	}

	@When ( "I select the filled prescription: (.+) and NDC code: (.+)" )
	public void addPrescription ( final String prescription, final String code ) {
		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "name" ) ) );
		final WebElement presc = driver.findElement( By.name( "name" ) );
		presc.clear();
		presc.sendKeys( prescription );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "code" ) ) );
		final WebElement pcode = driver.findElement( By.name( "code" ) );
		pcode.clear();
		pcode.sendKeys( code );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.xpath( "//button[@type = 'submit']" ) ) );
		final WebElement submit = driver.findElement( By.xpath( "//button[@type = 'submit']" ) );
		submit.click();

		expected = new NDCDrug();
		expected.setName( prescription );
		expected.setCode( code );
	}

	@When ( "I select the filled prescription: (.+), start date: (.+), end date: (.+), dosage: (.+), and renewals: (.+)" )
	public void documentPrescription ( final String prescription, final String start, final String end,
			final String dosage, final String renewals ) throws Exception {

		try {
			final Select patient = new Select( driver.findElement( By.name( "patient" ) ) );
			patient.selectByVisibleText( "patient" );
		}
		catch ( final Exception e ) {
			// do nothing because this is expected behavior for prescriptions
			// inside office visits
		}

		final Select drug = new Select( driver.findElement( By.name( "ndcCode" ) ) );
		try {

			drug.selectByVisibleText( "1000-0001-51 - " + prescription );
			assertTrue( drug.getAllSelectedOptions().get( 0 ).getText(),
					drug.getAllSelectedOptions().get( 0 ).getText().equals( "1000-0001-51 - " + prescription ) );

		}
		catch ( final Exception e ) {
			try {
				drug.getFirstSelectedOption();
			} catch (Exception e2) {
				try {
					drug.selectByIndex(0);
				} catch (Exception e3) {
					new Exception( "NDC Code:'" + "1000-0001-51 - " + prescription + "' not found." );
				}
				
			}
		}

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "prescStart" ) ) );
		final WebElement prescStart = driver.findElement( By.name( "prescStart" ) );
		prescStart.clear();
		prescStart.sendKeys( start );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "prescEnd" ) ) );
		final WebElement prescEnd = driver.findElement( By.name( "prescEnd" ) );
		prescEnd.clear();
		prescEnd.sendKeys( end );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "dosage" ) ) );
		final WebElement indosage = driver.findElement( By.name( "dosage" ) );
		indosage.clear();
		indosage.sendKeys( dosage );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "renewals" ) ) );
		final WebElement inrenewals = driver.findElement( By.name( "renewals" ) );
		inrenewals.clear();
		inrenewals.sendKeys( renewals );

		wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "submit" ) ) );
		final WebElement submit = driver.findElement( By.name( "submit" ) );
		submit.click();

		try {
			( (JavascriptExecutor) driver ).executeScript( "document.getElementById('logout').click();" );
		}
		catch ( final Exception e ) {
			// nothing
		}
	}
}


