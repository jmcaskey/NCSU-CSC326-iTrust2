package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the Prescription model. In all
 * requests made to this controller, the {id} provided is a Long that is the
 * ID of the prescription desired.
 * @author Ray Black
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIPrescriptionController extends APIController {

    /**
     * Creates a new Prescription from the RequestBody provided.
     *
     * @param pf
     *            The Prescription Form to be validated and saved to the database as a prescription.
     * @return response
     */
    @PostMapping ( BASE_PATH + "/prescriptions" )
    public ResponseEntity createPrescription ( @RequestBody final PrescriptionForm pf ) {
    	try {
        final Prescription script = new Prescription( pf );
        //Commented out because Prescription forms no longer track the id of their prescription.
//        if ( null != Prescription.getById( script.getId() ) ) {
//            return new ResponseEntity( "A prescription with ID: " + pf.getId() + " already exists",
//                    HttpStatus.CONFLICT );
//        }
            script.save();
            final User self = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() ); //Get the HCP for logging
            LoggerUtil.log( TransactionType.PRESCRIPTION_ADD, self.getUsername(), pf.getPatient(),
                    "Prescription for " + pf.getNdc() + " created for patient " + pf.getPatient());
            return new ResponseEntity( script, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    "Prescription for " + pf.getNdc() + " could not be validated because of " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }
    
    /**
     * Retrieves a list of all prescriptions in the database
     *
     * @return list of prescriptions
     */
    @GetMapping ( BASE_PATH + "/prescriptions" )
    public List<Prescription> getPrescriptions () {
        return Prescription.getAll();
    }
    
    /**
     * Retrieves a list of all Prescriptions in the database for the
     * current logged in patient
     * @return list of the current user's prescriptions
     */
    @GetMapping ( BASE_PATH + "/prescriptions/myprescriptions" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public List<Prescription> getMyPrescriptions () {
        final User self = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
        LoggerUtil.log( TransactionType.PRESCRIPTION_VIEW, self );
        return Prescription.getForPatient( self.getId() );
    }
    
    /**
     * Deletes the prescription with the id provided. This will remove all traces
     * from the system and cannot be reversed.
     *
     * @param id
     *            The id of the prescription to delete
     * @return response
     */
    @DeleteMapping ( BASE_PATH + "/prescriptions/{id}" )
    public ResponseEntity deletePrescription ( @PathVariable final Long id ) {
        final Prescription script = Prescription.getById( id );
        if ( null == script ) {
            return new ResponseEntity( "No prescription found for " + id, HttpStatus.NOT_FOUND );
        }
        try {
            script.delete();
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( "Could not delete prescription with ID " + id + " because of " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }
    
    
	
}
