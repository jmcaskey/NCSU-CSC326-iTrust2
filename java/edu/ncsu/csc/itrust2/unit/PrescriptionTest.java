package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.NDCDrugForm;
import edu.ncsu.csc.itrust2.forms.admin.DeleteNDCDrugForm;
import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.NDCDrug;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

import java.text.ParseException;

/**
 * Tests for the Prescription class and also for the (enclosed) NDCDrug class
 *
 * @author Felix Kim
 *
 */
public class PrescriptionTest {

    /**
     * Test the NDCDrugForm class.
     */
    @Test
    public void testNDCDrugForm () {
        final NDCDrug ndc = new NDCDrug();
        ndc.setName( "Prozac" );
        
        // First make sure incorrect code is not accepted
        boolean mishap = false;
        try {
        		ndc.setCode("0777-3105-0");
        }
        catch (IllegalArgumentException e) {
        		mishap = true;
        }
        assertEquals(mishap, true);
        
        // Use legitimate code this time
        ndc.setCode( "0777-3105-02" );
        ndc.save();
        
        // Make sure can get this newly created drug by code
        NDCDrug tempNdc = NDCDrug.getByCode(ndc.getCode());
        assertTrue(tempNdc != null);
        assertEquals(NDCDrug.getAll().get(0).getCode(), ndc.getCode());
        
        final NDCDrugForm ndcF = new NDCDrugForm( ndc );
        assertEquals( ndc.getName(), ndcF.getName() );
        assertEquals( ndc.getCode(), ndcF.getCode() );
        assertEquals( ndc.getId(), new Long(Long.parseLong(ndcF.getId() )));
        final NDCDrug ndc2 = new NDCDrug(ndcF);
        assertEquals( ndc2.getName(), ndcF.getName() );
        assertEquals( ndc2.getCode(), ndcF.getCode() );
        assertEquals( ndc2.getId(), new Long(Long.parseLong(ndcF.getId() )));
        final NDCDrugForm ndcF2 = new NDCDrugForm();
        ndcF2.setCode("0777-3105-02");
        ndcF2.setName("Prozac");
        ndcF2.setId("4");
        assertEquals( ndc.getName(), ndcF2.getName() );
        assertEquals( ndc.getCode(), ndcF2.getCode() );
        assertEquals( new Long(4), new Long(Long.parseLong(ndcF2.getId())));
        
        ndc.delete();
    }
    
    /**
     * Test the DeleteNDCDrugForm class.
     */
    @Test
    public void testDeleteNDCDrugForm() {
    		final DeleteNDCDrugForm delF = new DeleteNDCDrugForm();
    		delF.setCode("1111-2222-33");
    		delF.setConfirm("Y");
    		assertEquals(delF.getCode(), "1111-2222-33");
    		assertEquals(delF.getConfirm(), "Y");
    }
    
    /**
     * Test the PrescriptionForm class. Also tests ability to generate a Prescription object from a form. 
     */
    @Test
    public void testPrescriptionForm () throws ParseException {
    	
    		// Make sure there is an NDC code in the system
    		final NDCDrug ndc = new NDCDrug();
    		ndc.setName( "Prozac" );
    		ndc.setCode("0777-3105-02");
    		ndc.save();
    		
    		// Make sure there is a patient in the system
    		final User patient = new User( "jimmyNeutron", "123456", Role.ROLE_PATIENT, 1 );
    		patient.save();
    	
    		final PrescriptionForm pForm = new PrescriptionForm();
    		
    		pForm.setId(1L);
    		pForm.setNdc("0777-3105-02");
    		pForm.setPatient("jimmyNeutron");
    		pForm.setDosage("23");
    		pForm.setStartDate("11/22/2018");
    		pForm.setEndDate("11/22/2019");
    		pForm.setNumRenewals("24");
    		
    		final Prescription prescr = new Prescription(pForm);
    		prescr.save();
    		
    		assertEquals(pForm.getOfficeVisitId(), new Long(1L));
    		assertTrue(prescr.getId() != null); 
    		assertEquals(pForm.getNdc(), prescr.getNdc().getCode());    		
    		assertEquals(patient.getUsername(), prescr.getPatient().getUsername());
    		assertEquals(pForm.getDosage(), prescr.getDosage().toString());
    		assertEquals(pForm.getStartDate(), "11/22/2018");
    		assertTrue(prescr.getStartDate() != null);
    		assertEquals(pForm.getEndDate(), "11/22/2019");
    		assertTrue(prescr.getEndDate() != null);
    		assertEquals(new Integer(pForm.getNumRenewals()), prescr.getNumRenewals());
    		assertTrue(Prescription.getForPatient(pForm.getPatient()) != null);
    		assertTrue(Prescription.getAll() != null);
    		assertTrue(Prescription.getById(prescr.getId()) != null);
    		
    		prescr.delete();
    		ndc.delete();		
    }

}

