package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class HCPEditDemographicsStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";
    WebDriverWait           wait    = new WebDriverWait( driver, 2 );

    @When ( "I log in to iTrust2 as the HCP" )
    public void loginAsHCPD () throws Exception {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "push" ) ) );

    }

    @When ( "I navigate to the Office Visit page to edit demographics" )
    public void editDemographicsP () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "notes" ) ) );

    }

    @When ( "I select a patient and edit their demographics" )
    public void selectPatientToEdit () throws Exception {
        try {
            wait.until( ExpectedConditions.visibilityOfElementLocated(
                    By.xpath( "//*[contains(text()[normalize-space()], '(patient)')]" ) ) );
            final WebElement patient = driver
                    .findElement( By.xpath( "//*[contains(text()[normalize-space()], '(patient)')]" ) );
            patient.click();
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated( By.xpath( "//a[text() = 'Edit Demographics']" ) ) );
            final WebElement edit = driver.findElement( By.xpath( "//a[text() = 'Edit Demographics']" ) );
            edit.click();
            wait.until( ExpectedConditions
                    .visibilityOfElementLocated( By.xpath( "//h1[text() = 'Edit Patient Demographics']" ) ) );
        }
        catch ( final Exception e ) {
            driver.get( baseUrl + "/hcp/editDemographics/patient" );
        }
    }

    @When ( "I fill in new, updated, patient demographics" )
    public void fillDemographics () {
        try {
            Thread.sleep( 2000 );
        }
        catch ( final Exception e ) {
        }

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "098-765-4321" );

        final WebElement submit = driver.findElement( By.xpath( "//button[text() = 'Submit']" ) );
        submit.click();
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "success" ) ) );
    }

    @Then ( "The patient demographics are updated" )
    public void updatedSuccessfully () {
        assertTrue( driver.findElement( By.name( "success" ) ).getText().contains( "success" ) );
    }

    @Then ( "The new patient demographics can be viewed" )
    public void viewDemographics () throws Exception {

        driver.get( baseUrl + "/hcp/editDemographics/patient" );

        try {
            Thread.sleep( 2000 );
        }
        catch ( final Exception e ) {
        }

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        assertEquals( driver.findElement( By.tagName( "body" ) ).getAttribute( "innerHTML" ), "098-765-4321",
                phone.getAttribute( "value" ) );
    }
}
