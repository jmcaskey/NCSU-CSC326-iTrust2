package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.NDCDrug;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with NDCDrugs
 *
 * @author Felix Kim
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIPrescriptionTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Test set up
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests getting a non existent NDCDrug and ensures that the correct status
     * is returned.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentNDCDrug () throws Exception {
        mvc.perform( get( "/api/v1/ndcDrugs/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests NDCDrug API
     *
     * @throws Exception
     */
    @Test
    public void testNDCDrugAPI () throws Exception {
        mvc.perform( delete( "/api/v1/ndcDrugs" ) );
        
        NDCDrug ndc = new NDCDrug();
        ndc.setName( "Silver bullet" );
        ndc.setCode( "1111-5555-66" );
        
        mvc.perform( post( "/api/v1/ndcDrugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ndc ) ) );
        mvc.perform( get( "/api/v1/ndcDrugs" ) ).andExpect( status().isOk() );

        // Cannot create same NDC drug twice
        mvc.perform( post( "/api/v1/ndcDrugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ndc ) ) ).andExpect( status().isConflict() );
    }
    
    /**
     * Tests getting a non existent Prescription and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentPrescription () throws Exception {
        mvc.perform( get( "/api/v1/prescriptions/-1" ) ).andExpect( status().isMethodNotAllowed() );
    }

    /**
     * Tests deleting a non existent Prescription and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    public void testDeleteNonExistentPrescription () throws Exception {
        mvc.perform( delete( "/api/v1/prescriptions/-1" ) ).andExpect( status().isNotFound() );
    }
}
