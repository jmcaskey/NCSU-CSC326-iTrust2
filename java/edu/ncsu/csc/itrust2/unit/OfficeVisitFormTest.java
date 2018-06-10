package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.text.ParseException;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests for the OfficeVisitForm class
 *
 * @author Felix Kim
 *
 */
public class OfficeVisitFormTest {

    /**
     * Test the OfficeVisitForm class.
     * 
     * @throws ParseException
     */
    @Test
    public void officeVisitForm () throws ParseException {
    		// Set up the patients and hcp
    		final User patient = new User( "Waldo", "123456", Role.ROLE_PATIENT, 1 );
    		final User patient2 = new User("Baby", "123456", Role.ROLE_PATIENT, 1);
    		final User patient3 = new User("Kiddie", "123456", Role.ROLE_PATIENT, 1);
    		final User hcp = new User( "Marcus", "123456", Role.ROLE_HCP, 1 );
    		patient.save();
    		patient2.save();
    		patient3.save();
    		hcp.save();
    		
    		// Set up hospital
    		 /* Create a Hospital to use too */
    		final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
    		hospital.save();
    		
    		// Set up an ICD code
    		final ICDCode icd = new ICDCode();
    		icd.setName( "random problem" );
    		icd.setCode( "T22.8" );
    		icd.save();
    		
    		// Set up the patient demographics
    		final User mom = new User( "patientTestMom", "123456", Role.ROLE_PATIENT, 1 );
    		mom.save();
    		final User dad = new User( "patientTestDad", "123456", Role.ROLE_PATIENT, 1 );
    		dad.save();
    		
    		final PatientForm form = new PatientForm();
    		form.setMother( mom.getUsername() );
    		form.setFather( dad.getUsername() );
    		form.setFirstName( "patient" );
    		form.setPreferredName( "patient" );
    		form.setLastName( "mcpatientface" );
    		form.setEmail( "bademail@ncsu.edu" );
    		form.setAddress1( "Some town" );
    		form.setAddress2( "Somewhere" );
    		form.setCity( "placecity" );
    		form.setState( State.AL.getName() );
    		form.setZip( "27606" );
    		form.setPhone( "111-111-1111" );
    		form.setDateOfBirth( "01/01/1982" );
    		form.setBloodType( BloodType.ABPos.getName() );
    		form.setEthnicity( Ethnicity.Asian.getName() );
    		form.setGender( Gender.Male.getName() );
    		form.setSelf( patient.getUsername() );
    		
    		// Adult
    		final Patient testPatient = new Patient( form );
    		testPatient.save();
    		
    		// Infant
    		form.setDateOfBirth("01/01/2029");
    		form.setSelf(patient2.getUsername());
    		final Patient testPatient2 = new Patient(form);
    		testPatient2.save();
    		
    		// Adolescent
    		form.setDateOfBirth("01/01/2020");
    		form.setSelf(patient3.getUsername());
    		final Patient testPatient3 = new Patient(form);
    		testPatient3.save();
    		
    		assertTrue(testPatient != null && testPatient.getDateOfBirth() != null);
        
        final OfficeVisitForm ovF = new OfficeVisitForm();
        final OfficeVisitForm ovF2 = new OfficeVisitForm();
        final OfficeVisitForm ovF3 = new OfficeVisitForm();
        
        ovF.setPatient(patient.getUsername());
        ovF2.setPatient(patient2.getUsername());
        ovF3.setPatient(patient3.getUsername());
        
        ovF.setHcp(hcp.getUsername());
        ovF2.setHcp(hcp.getUsername());
        ovF3.setHcp(hcp.getUsername());
        
        ovF.setDate("11/19/2030");
        ovF2.setDate("11/20/2030");
        ovF3.setDate("11/21/2030");
        
        ovF.setTime("11:20 AM");
        ovF.setId("5");
        ovF.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ovF.setHospital( "iTrust Test Hospital 2" );
        ovF.setNotes("These are a set of notes.");
        ovF.setIcdCode("T22.8");
        ovF.setDiastolic(99);
        ovF.setHdl(80);
        ovF.setHeight(99.9f);
        ovF.setHouseSmokingStatus(HouseholdSmokingStatus.INDOOR);
        ovF.setHeadCircumference(99.9f);
        ovF.setLdl(500);
        ovF.setPatientSmokingStatus(PatientSmokingStatus.SOMEDAYS);
        ovF.setSystolic(99);
        ovF.setTri(400);
        ovF.setWeight(98.2f);
        
        ovF2.setTime("11:20 AM");
        ovF2.setId("5");
        ovF2.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ovF2.setHospital( "iTrust Test Hospital 2" );
        ovF2.setNotes("These are a set of notes.");
        ovF2.setIcdCode("T22.8");
        ovF2.setHeight(99.9f);
        ovF2.setHouseSmokingStatus(HouseholdSmokingStatus.INDOOR);
        ovF2.setHeadCircumference(99.9f);
        ovF2.setWeight(98.2f);
        
        ovF3.setTime("11:20 AM");
        ovF3.setId("5");
        ovF3.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ovF3.setHospital( "iTrust Test Hospital 2" );
        ovF3.setNotes("These are a set of notes.");
        ovF3.setIcdCode("T22.8");
        ovF3.setDiastolic(99);
        ovF3.setHeight(99.9f);
        ovF3.setHouseSmokingStatus(HouseholdSmokingStatus.INDOOR);
        ovF3.setSystolic(99);
        ovF3.setWeight(98.2f);
        
        OfficeVisit ov, ov2, ov3;
        
        boolean completed = false;
        
		try {
			ov = new OfficeVisit(ovF);
			ov2 = new OfficeVisit(ovF2);
			ov3 = new OfficeVisit(ovF3);
			
			assertTrue(ov2 != null);
			assertTrue(ov3 != null);
			
			final Patient p = Patient.getPatient(ov.getPatient().getUsername());
			assertTrue(p.getDateOfBirth() != null);
			
	        assertEquals(ov.getPatient().getUsername(), ovF.getPatient());
	        assertEquals(ov.getHcp().getUsername(), ovF.getHcp());
	        assertEquals(ov.getId(), new Long(Long.parseLong(ovF.getId())));
	        assertEquals(ov.getType().toString(), ovF.getType());
	        assertEquals(ov.getHospital().getName(), ovF.getHospital());
	        assertEquals(ov.getNotes(), ovF.getNotes());
	        assertEquals(ov.getIcdCode().getCode(), ovF.getIcdCode());
	        assertTrue(ov.getDate() != null && ovF.getDate() != null);
	        
	        BasicHealthMetrics bhm = new BasicHealthMetrics(ovF);
	        assertEquals(bhm.getDiastolic(), ovF.getDiastolic());
	        assertEquals(bhm.getHdl(), ovF.getHdl());
	        assertEquals(bhm.getHeight(), ovF.getHeight());
	        assertEquals(bhm.getHouseSmokingStatus(), ovF.getHouseSmokingStatus());
	        assertEquals(bhm.getHeadCircumference(), ovF.getHeadCircumference());
	        assertEquals(bhm.getLdl(), ovF.getLdl());
	        assertEquals(bhm.getPatientSmokingStatus(), ovF.getPatientSmokingStatus());
	        assertEquals(bhm.getSystolic(), ovF.getSystolic());
	        assertEquals(bhm.getTri(), ovF.getTri());
	        assertEquals(bhm.getWeight(), ovF.getWeight());
	        
	        BasicHealthMetrics bhmClone = new BasicHealthMetrics(ovF);
	        BasicHealthMetrics bhm2 = new BasicHealthMetrics(ovF2);
	        BasicHealthMetrics bhm3 = new BasicHealthMetrics(ovF3);
	        
	        assertEquals(bhm.hashCode(), bhmClone.hashCode());
	        Integer hashCode2 = bhm2.hashCode();
	        assertFalse(hashCode2.equals(new Integer(bhm.hashCode())));
	        assertFalse(hashCode2.equals(new Integer(bhm3.hashCode())));
	        
    			assertEquals(bhm, ov.getBasicHealthMetrics());
    			assertEquals(bhm, bhm);
    			assertFalse(bhm2.equals(bhm));
    			assertFalse(bhm3.equals(bhm));
    			assertFalse(bhm.equals(null));
    			
    			completed = true;
	        
		} catch (NumberFormatException e) {
			// Do nothing
		} catch (ParseException e) {
			// Do nothing
		}
		
		assertEquals(completed, true);
    } 
}

