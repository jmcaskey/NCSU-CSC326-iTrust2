package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test MVC for correct page loads that user should see
 *
 * @author devanisaac
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class MVCtesting {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests forgotPassword page loading
     */
    @Test
    public void testForgotPass () {

        try {
            mvc.perform( get( "/forgotPassword" ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Test loading editPassword page
     */
    @Test
    public void testEditPass () {
        try {
            mvc.perform( get( "/patient/editPassword" ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            mvc.perform( get( "/hcp/editPassword" ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            mvc.perform( get( "/admin/editPassword" ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Tests email sending to user
     */
    @Test
    public void testEmail () throws Exception {

        Patient.deleteAll( Patient.class );
        Personnel.deleteAll( Personnel.class );

        try {
            mvc.perform( post( "/passwordReset/hcp" ) ).andExpect( status().isNotFound() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // andExpect( status().isFound() );
        try {
            mvc.perform( post( "/passwordReset/thisuserdontexist" ) ).andExpect( status().isNotFound() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Clear out all patients before running these tests.
        Patient.deleteAll( Patient.class );
        // Clear out all hcp before running these tests
        Personnel.deleteAll( Personnel.class );

        final UserForm p = new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 );
        final UserForm p2 = new UserForm( "jack", "123456", Role.ROLE_HCP, 1 );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p2 ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "6/15/1977" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );

        final PersonnelForm person = new PersonnelForm();
        person.setAddress1( "1 Test Street" );
        person.setAddress2( "Some Location" );
        person.setCity( "Viipuri" );
        person.setEmail( "antti@itrust.fi" );
        person.setFirstName( "Antti" );
        person.setLastName( "Walhelm" );
        person.setPhone( "123-456-7890" );
        person.setSelf( "jack" );
        person.setState( State.NC.toString() );
        person.setZip( "27514" );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        try {
            mvc.perform( post( "/passwordReset/antti" ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( person ) ) );

        try {
            mvc.perform( post( "/passwordReset/jack" ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
