package edu.ncsu.csc.itrust2.controllers.api;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the edit patient demographics
 * model. In all requests made to this controller, the {id} provided is a Long
 * that is the primary key id of the office visit requested.
 *
 * @author Alec Rohloff
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIEditPatientDemographicsController extends APIController {

    /**
     * Retrieves demographics information for a single patient
     *
     * @param username
     *            the username of patient to retrieve demographics for
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/demographics/{username}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public Patient getDemographics ( @PathVariable ( "username" ) final String username ) {
        final User pat = User.getByName( username );
        return Patient.getPatient( pat );
    }

    /**
     * Processes the Edit Demographics form for a Patient
     *
     * @param form
     *            Form from the user to parse and validate
     * @return Page to show to the user
     */
    @PostMapping ( BASE_PATH + "/hcp/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity submitDemographics ( @RequestBody final PatientForm form ) {
        Patient p = null;
        try {
            p = new Patient( form );
        }
        catch ( final Exception e ) {
            e.printStackTrace( System.out );
            return new ResponseEntity( "Invalid form", HttpStatus.BAD_REQUEST );
        }
        final Set<ConstraintViolation<PatientForm>> formViolations = Validation.buildDefaultValidatorFactory()
                .getValidator().validate( form );
        if ( formViolations.size() != 0 ) {
            String errMsg = "";
            final Iterator<ConstraintViolation<PatientForm>> violationIter = formViolations.iterator();
            while ( violationIter.hasNext() ) {
                final ConstraintViolation<PatientForm> v = violationIter.next();
                errMsg += v.getPropertyPath() + " " + v.getMessage() + ". ";
            }

            return new ResponseEntity( errMsg, HttpStatus.BAD_REQUEST );
        }
        final Set<ConstraintViolation<Patient>> violations = Validation.buildDefaultValidatorFactory().getValidator()
                .validate( p );
        if ( violations.size() != 0 ) {
            return new ResponseEntity( "One or more errors", HttpStatus.BAD_REQUEST );
        }

        // Delete the patient so that the cache has to refresh.
        final Patient oldPatient = Patient.getPatient( p.getSelf().getUsername() );
        if ( oldPatient != null ) {
            oldPatient.delete();
        }
        p.save();

        LoggerUtil.log( TransactionType.ENTER_EDIT_DEMOGRAPHICS,
                SecurityContextHolder.getContext().getAuthentication().getName() );
        return new ResponseEntity( p, HttpStatus.OK );

    }

}
