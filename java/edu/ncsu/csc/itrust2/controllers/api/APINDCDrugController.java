package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.NDCDrugForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.NDCDrug;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the NDCDrug model. In all requests
 * made to this controller, the {id} provided is a String that is the code of
 * the NDCDrug desired.
 *
 * @author Felix Kim
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )

public class APINDCDrugController extends APIController {

    /**
     * Retrieves a list of all NDC's in the database
     *
     * @return list of NDC's
     */
    @GetMapping ( BASE_PATH + "/ndcDrugs" )
    public List<NDCDrug> getNDCDrugs () {
        return NDCDrug.getAll();
    }

    /**
     * Retrieves the NDCDrug specified by the code provided
     *
     * @param id
     *            The code of the NDCDrug
     * @return response
     */
    @GetMapping ( BASE_PATH + "/ndcDrugs/{id}" )
    public ResponseEntity getNDCDrug ( @PathVariable ( "id" ) final String id ) {
        final NDCDrug ndc = NDCDrug.getByCode( id );
        if ( null != ndc ) {
            // Commented out because of uncertainty regarding logging for NDC's
            // LoggerUtil.log( TransactionType.VIEW_NDCDRUG, ndc.getCode() );
        }
        return null == ndc ? new ResponseEntity( "No NDCDrug found for code " + id, HttpStatus.NOT_FOUND )
                : new ResponseEntity( ndc, HttpStatus.OK );
    }

    /**
     * Creates a new NDCDrug from the RequestBody provided.
     *
     * @param ndcF
     *            The NDC to be validated and saved to the database.
     * @return response
     */
    @PostMapping ( BASE_PATH + "/ndcDrugs" )
    public ResponseEntity createNDCDrug ( @RequestBody final NDCDrugForm ndcF ) {
        final NDCDrug ndc = new NDCDrug( ndcF );
        if ( null != NDCDrug.getByCode( ndc.getCode() ) ) {
            return new ResponseEntity( "The NDC " + ndc.getCode() + " already exists", HttpStatus.CONFLICT );
        }
        try {
            ndc.save();
            // Commented out because of uncertainty regarding logging for NDC's
             LoggerUtil.log( TransactionType.NDC_ADD, ndc.getCode() );
            return new ResponseEntity( ndc, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    "Error occured while validating or saving " + ndc.getCode() + " because of " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }

    }
}
