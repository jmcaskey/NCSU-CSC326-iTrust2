package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the ICDCode model. In all
 * requests made to this controller, the {id} provided is a String that is the
 * code of the ICDCode desired.
 *
 * @author Felix Kim
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )

public class APIICDController extends APIController {

    /**
     * Retrieves a list of all ICD codes in the database
     *
     * @return list of ICD codes
     */
    @GetMapping ( BASE_PATH + "/icdCodes" )
    public List<ICDCode> getICDCodes () {
        return ICDCode.getAll();
    }

    /**
     * Retrieves the ICDCode specified by the code provided
     *
     * @param id
     *            The code of the ICDCode
     * @return response
     */
    @GetMapping ( BASE_PATH + "/icdCodes/{id}" )
    public ResponseEntity getICDCode ( @PathVariable ( "id" ) final String id ) {
        final ICDCode icd = ICDCode.getByCode( id );
        if ( null != icd ) {
        		// Commented out because of uncertainty regarding logging for ICD codes
            // LoggerUtil.log( TransactionType.VIEW_ICDCODE, icd.getCode() );
        }
        return null == icd ? new ResponseEntity( "No ICDCode found for code " + id, HttpStatus.NOT_FOUND )
                : new ResponseEntity( icd, HttpStatus.OK );
    }

    /**
     * Creates a new ICDCode from the RequestBody provided.
     *
     * @param icdF
     *            The ICD Code to be validated and saved to the database.
     * @return response
     */
    @PostMapping ( BASE_PATH + "/icdCodes" )
    public ResponseEntity createICDCode ( @RequestBody final ICDCodeForm icdF ) {
        final ICDCode icd = new ICDCode( icdF );
        if ( null != ICDCode.getByCode( icd.getCode() ) ) {
            return new ResponseEntity( "The ICD code " + icd.getCode() + " already exists",
                    HttpStatus.CONFLICT );
        }
        try {
            icd.save();
            // Commented out because of uncertainty regarding logging for ICD codes
            LoggerUtil.log( TransactionType.ICD_ADD, icd.getCode() );
            return new ResponseEntity( icd, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    "Error occured while validating or saving " + icd.getCode() + " because of " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }

    }
}
