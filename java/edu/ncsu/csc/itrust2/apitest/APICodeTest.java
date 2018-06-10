package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with ICD Codes
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APICodeTest {

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
     * Tests getting a non existent ICD code and ensures that the correct status
     * is returned.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentICDCode () throws Exception {
        mvc.perform( get( "/api/v1/icdCodes/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests ICDCode API
     *
     * @throws Exception
     */
    @Test
    public void testICDCodeAPI () throws Exception {
        mvc.perform( delete( "/api/v1/icdCodes" ) );
        
        ICDCode icd = new ICDCode();
        icd.setName( "some problem" );
        icd.setCode( "A22.2" );
        
        mvc.perform( post( "/api/v1/icdCodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( icd ) ) );
        mvc.perform( get( "/api/v1/icdCodes" ) ).andExpect( status().isOk() );

        // Cannot create same ICD code twice
        mvc.perform( post( "/api/v1/icdCodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( icd ) ) ).andExpect( status().isConflict() );
    }

}
