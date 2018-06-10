package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.forms.admin.DeleteICDCodeForm;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;

/**
 * Tests for the ICDCodeForm class
 *
 * @author Felix Kim
 *
 */
public class ICDFormTest {

    /**
     * Test the ICDCodeForm class.
     */
    @Test
    public void testICDCodeForm () {
        final ICDCode icd = new ICDCode();
        icd.setName( "some problem" );
        icd.setCode( "A22.2" );
        icd.setId(4L);
        final ICDCodeForm icdF = new ICDCodeForm( icd );
        assertEquals( icd.getName(), icdF.getName() );
        assertEquals( icd.getCode(), icdF.getCode() );
        assertEquals( icd.getId(), new Long(Long.parseLong(icdF.getId() )));
        final ICDCode icd2 = new ICDCode(icdF);
        assertEquals( icd2.getName(), icdF.getName() );
        assertEquals( icd2.getCode(), icdF.getCode() );
        assertEquals( icd2.getId(), new Long(Long.parseLong(icdF.getId() )));
        final ICDCodeForm icdF2 = new ICDCodeForm();
        icdF2.setCode("A22.2");
        icdF2.setName("some problem");
        icdF2.setId("4");
        assertEquals( icd.getName(), icdF2.getName() );
        assertEquals( icd.getCode(), icdF2.getCode() );
        assertEquals( icd.getId(), new Long(Long.parseLong(icdF2.getId())));
    }
    
    /**
     * Test the DeleteICDCodeForm class.
     */
    @Test
    public void testDeleteICDCodeForm() {
    		final DeleteICDCodeForm delF = new DeleteICDCodeForm();
    		delF.setCode("E78.0");
    		delF.setConfirm("Y");
    		assertEquals(delF.getCode(), "E78.0");
    		assertEquals(delF.getConfirm(), "Y");
    }
}
