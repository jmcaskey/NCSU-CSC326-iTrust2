package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;

/**
 * Step definitions for AddHosptial feature
 */
public class AddDiagnosesStepDefs {

    private final WebDriver driver       = new HtmlUnitDriver( true );
    private final String    baseUrl      = "http://localhost:8080/iTrust2";

    private ICDCode         expectedDiag = null;

    /**
     * Diagnosis doesn't exist
     */
    @Given ( "The desired diagnosis: (.+) does not exist" )
    public void noDiagnosis ( final String name ) {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        try {
            driver.get( baseUrl + "/admin/deleteDiagnosis" );
            driver.findElement( By.id( name ) ).click();
            driver.findElement( By.className( "checkbox" ) ).click();
            driver.findElement( By.className( "btn" ) ).click();
        }
        catch ( final Exception e ) {
            // Assume doesn't exist; print stack trace and carry on.
            e.printStackTrace();
        }
        driver.findElement( By.id( "logout" ) ).click();

    }

    /**
     * Admin login
     */
    @When ( "I log in to iTrust2 as an Admin" )
    public void loginAdminD () {
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

    /**
     * Add hospital page
     */
    @When ( "I navigate to the Add Diagnosis page" )
    public void addDiagnosisPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addicdcode').click();" );
    }

    /**
     * Fill diagnosis forms
     */
    @When ( "I fill in information on the diagnoses with diagnoses: (.+), and  ICD-10 id: (.+)$" )
    public void fillDiagnosisFields ( final String diagnosisName, final String id ) {
        final WebElement name = driver.findElement( By.id( "name" ) );
        name.clear();
        name.sendKeys( diagnosisName );

        final WebElement idc = driver.findElement( By.id( "code" ) );
        idc.clear();
        idc.sendKeys( id );
        expectedDiag = new ICDCode();
        expectedDiag.setName( diagnosisName );
        expectedDiag.setCode( id );

        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * Create diagnosis successfully
     */
    @Then ( "The diagnosis is added successfully" )
    public void createdSuccessfully () {
        assertTrue( driver.getPageSource().contains( "Diagnosis added successfully" ) );
    }

    /**
     * Ensures that the correct diagnosis have been entered
     *
     * @throws InterruptedException
     */
    @Then ( "The diagnoses values are correct" )
    public void healthMetricsCorrectInfant () throws InterruptedException {
        ICDCode actualDiag = null;
        for ( int i = 1; i <= 10; i++ ) {
            try {
                actualDiag = ICDCode.getAll().get( 0 );
                break;
            }
            catch ( final Exception e ) {
                if ( i == 10 && actualDiag == null ) {
                    fail( "Could not get icdcode out of database" );
                }
                Thread.sleep( 1000 );
            }
        }
        assertEquals( expectedDiag.getName(), actualDiag.getName() );
        assertEquals( expectedDiag.getCode(), actualDiag.getCode() );
    }
}
