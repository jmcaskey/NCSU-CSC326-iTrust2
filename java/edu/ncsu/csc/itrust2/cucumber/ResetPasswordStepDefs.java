package edu.ncsu.csc.itrust2.cucumber;

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
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class ResetPasswordStepDefs {

    private final WebDriver driver       = new HtmlUnitDriver( true );
    private final String    baseUrl      = "http://localhost:8080/iTrust2";

    private final String    hospitalName = "Office Visit Hospital" + ( new Random() ).nextInt();

    WebDriverWait           wait         = new WebDriverWait( driver, 2 );

    @Given ( "A user exists with an email" )
    public void userSetup () {
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
            email.sendKeys( "csc3262015test@gmail.com" );

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

    @When ( "I select forgot password" )
    public void selectForgotPassword () {
        final WebElement forgot = driver.findElement( By.xpath( "//a[text() = Forgot Password?]" ) );
        forgot.click();
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.xpath( "//input[@type = 'submit']" ) ) );
    }

    @When ( "I enter the user's username" )
    public void enterUsername () throws InterruptedException {
        final WebElement username = driver.findElement( By.xpath( "//input[@type = 'text']" ) );
        username.clear();
        username.sendKeys( "patient" );

        final WebElement submit = driver.findElement( By.xpath( "//input[@type = 'submit']" ) );
        submit.click();
        Thread.sleep( 10000 );

    }

    @Then ( "An email is sent to the email" )
    public void emailSent () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.xpath( "//div[text() = 'Email sent with new password.']" ) ) );
            }

    @Then ( "The new password is valid" )
    public void validLogin () {

    }

}
