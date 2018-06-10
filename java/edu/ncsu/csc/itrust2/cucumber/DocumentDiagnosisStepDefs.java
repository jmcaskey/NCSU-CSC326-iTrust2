package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class DocumentDiagnosisStepDefs {

    private final WebDriver driver       = new HtmlUnitDriver( true );
    private final String    baseUrl      = "http://localhost:8080/iTrust2";

    private final String    hospitalName = "Office Visit Hospital" + ( new Random() ).nextInt();
    private ICDCode         expectedDiag;

    WebDriverWait           wait         = new WebDriverWait( driver, 2 );

    private void addCode ( final String name, final String code ) {
        final ICDCodeForm form = new ICDCodeForm();
        form.setCode( code );
        form.setName( name );

        ICDCode icd = null;
        try {
            icd = new ICDCode( form );
        }
        catch ( final Exception e ) {
            throw e;
        }
        icd.save();
    }

    @Given ( "The required entries exist for diagnosis" )
    public void personnelExists () throws Exception {
        OfficeVisit.deleteAll( OfficeVisit.class );
        ICDCode.deleteAll( ICDCode.class );
        addCode( "Flu", "J09.1" );
        addCode( "Careless Weed", "E00.1" );

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
    @Given ( "^A patient exists to diagnose with name: (.+) and date of birth: (.+)$" )
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

    @When ( "I log in to iTrust2 as HCP" )
    public void loginAsHCPD () {
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

    @When ( "I fill in information on the office visit for infants" )
    public void documentOVInfant () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "notes" ) ) );
        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( "Patient appears pretty much alive" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.cssSelector( "input[value=\"patient\"]" ) ) );
        final WebElement patient = driver.findElement( By.cssSelector( "input[value=\"patient\"]" ) );
        patient.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "type" ) ) );
        final WebElement type = driver.findElement( By.name( "type" ) );
        type.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "hospital" ) ) );
        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();

        final WebElement date = driver.findElement( By.name( "date" ) );
        date.clear();
        date.sendKeys( "12/19/2018" );

        final WebElement time = driver.findElement( By.name( "time" ) );
        time.clear();
        time.sendKeys( "9:30 AM" );

        expectedDiag = new ICDCode();

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

    @When ( "I fill in information on the office visit for children" )
    public void documentOVChild () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "notes" ) ) );
        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( "Patient appears pretty much alive" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.cssSelector( "input[value=\"patient\"]" ) ) );
        final WebElement patient = driver.findElement( By.cssSelector( "input[value=\"patient\"]" ) );
        patient.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "type" ) ) );
        final WebElement type = driver.findElement( By.name( "type" ) );
        type.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "hospital" ) ) );
        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();

        final WebElement date = driver.findElement( By.name( "date" ) );
        date.clear();
        date.sendKeys( "12/19/2017" );

        final WebElement time = driver.findElement( By.name( "time" ) );
        time.clear();
        time.sendKeys( "9:30 am" );

        expectedDiag = new ICDCode();

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

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "systolic" ) ) );
        final WebElement sysElem = driver.findElement( By.name( "systolic" ) );
        sysElem.clear();
        sysElem.sendKeys( "100" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "diastolic" ) ) );
        final WebElement diaElem = driver.findElement( By.name( "diastolic" ) );
        diaElem.clear();
        diaElem.sendKeys( "100" );

    }

    @When ( "I fill in information on the office visit for adults" )
    public void documentOVAdult () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "notes" ) ) );
        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( "Patient appears pretty much alive" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.cssSelector( "input[value=\"patient\"]" ) ) );
        final WebElement patient = driver.findElement( By.cssSelector( "input[value=\"patient\"]" ) );
        patient.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "type" ) ) );
        final WebElement type = driver.findElement( By.cssSelector( "input[value=\"GENERAL_CHECKUP\"]" ) );
        type.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "hospital" ) ) );
        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();

        final WebElement date = driver.findElement( By.name( "date" ) );
        date.clear();
        date.sendKeys( "12/19/2027" );

        final WebElement time = driver.findElement( By.name( "time" ) );
        time.clear();
        time.sendKeys( "9:30 am" );

        expectedDiag = new ICDCode();

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

        wait.until( ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector( "input[value=\"" + PatientSmokingStatus.NEVER.toString() + "\"]" ) ) );
        final WebElement smoking = driver
                .findElement( By.cssSelector( "input[value=\"" + PatientSmokingStatus.NEVER.toString() + "\"]" ) );
        smoking.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "systolic" ) ) );
        final WebElement sysElem = driver.findElement( By.name( "systolic" ) );
        sysElem.clear();
        sysElem.sendKeys( "100" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "diastolic" ) ) );
        final WebElement diaElem = driver.findElement( By.name( "diastolic" ) );
        diaElem.clear();
        diaElem.sendKeys( "100" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "hdl" ) ) );
        final WebElement hdlElem = driver.findElement( By.name( "hdl" ) );
        hdlElem.clear();
        hdlElem.sendKeys( "10" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "ldl" ) ) );
        final WebElement ldlElem = driver.findElement( By.name( "ldl" ) );
        ldlElem.clear();
        ldlElem.sendKeys( "100" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "tri" ) ) );
        final WebElement triElem = driver.findElement( By.name( "tri" ) );
        triElem.clear();
        triElem.sendKeys( "100" );

    }

    @When ( "I fill in information on the diagnosis with diagnosis: (.+), and  ICD-10 id: (.+)$" )
    public void documentDiagnosis ( final String diagnosisName, final String id ) throws InterruptedException {
        final Select select = new Select( driver.findElement( By.name( "icdCode" ) ) );
        // expectedDiag.setName( diagnosisName );
        // expectedDiag.setCode( id );
        try {
            select.selectByVisibleText( id + " - " + diagnosisName );
            expectedDiag.setName( diagnosisName );
            expectedDiag.setCode( id );
        }
        catch ( final Exception e ) {
            // do nothing because this is expected behavior for invalid test
        }
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "submit" ) ) );
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        submit.click();
        // Give the data time to save to the database
        Thread.sleep( 2000 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "messages" ) ) );
        final WebElement messages = driver.findElement( By.name( "messages" ) );
        //assertTrue( messages.getText(), false );

    }

    /**
     * Ensures that the correct health metrics have been entered
     *
     * @throws InterruptedException
     */
    @Then ( "The diagnosis for the office visit is correct" )
    public void diagnosisCorrect () throws InterruptedException {
        ICDCode actualDiag = null;
        for ( int i = 1; i <= 10; i++ ) {
            try {
                actualDiag = OfficeVisit.getOfficeVisits().get( 0 ).getIcdCode();
                break;
            }
            catch ( final Exception e ) {
                if ( i == 10 && actualDiag == null ) {
                    fail( "Could not get ICD Code out of database" );
                }
                Thread.sleep( 1000 );
            }
        }
        if ( actualDiag != null ) {
            assertEquals( expectedDiag.getName(), actualDiag.getName() );
            assertEquals( expectedDiag.getCode(), actualDiag.getCode() );
        }
    }

    /**
     * Ensures that the office visit was not recorded.
     */
    @Then ( "The diagnosis is documented unsuccessfully" )
    public void notDocumented () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "success" ) ) );
        final WebElement message = driver.findElement( By.name( "success" ) );

        final Select select = new Select( driver.findElement( By.name( "icdCode" ) ) );
        assertEquals( "<none>", select.getFirstSelectedOption().getText() );

        assertTrue( message.getText().contains( "success" ) );
    }

    @Then ( "The diagnosis is documented successfully" )
    public void documentedSuccessfully () {
        System.out.println( driver.findElement( By.tagName( "body" ) ).getAttribute( "innerHTML" ) );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "messages" ) ) );
        final WebElement message = driver.findElement( By.name( "messages" ) );

        assertFalse( message.getText(), message.getText().contains( "Error occurred creating office visit" ) );

    }
}
