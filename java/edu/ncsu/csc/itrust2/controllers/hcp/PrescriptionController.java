package edu.ncsu.csc.itrust2.controllers.hcp;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * Controller that enables a HCP add a prescription into the system outside 
 * of an office visit
 * @author Ray Black
 */
@Controller
public class PrescriptionController {
	
	/**
     * Creates the form page for the Add prescription page
     *
     * @param model
     *            Data for the front end
     * @return Page to show to the user
     */
    @RequestMapping ( value = "hcp/addPrescription" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String fillOutPrecription ( final Model model ) {
        model.addAttribute( "PrescriptionForm", new PrescriptionForm() );
        return "hcp/addPrescription"; 
    }
	
    /**
     * Parses the ICDCodeForm from the User
     *
     * @param form
     *            ICDCodeForm to validate and save
     * @param result
     *            Validation result
     * @param model
     *            Data from the front end
     * @return The page to show to the user
     */
    @PostMapping ( "hcp/addPrescription" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String fillOutPrescriptionSubmit ( @Valid @ModelAttribute ( "PrescriptionForm" ) final PrescriptionForm form,
            final BindingResult result, final Model model ) {
        Prescription script = null;
        try {
            script = new Prescription( form );
            if ( Prescription.getById( script.getId() ) != null ) {
                result.rejectValue( "id", "id.notvalid", "A Prescription with this ID already exists" );
            }
        }
        catch ( final Exception e ) {
        		result.reject( "prescriptionForm.notvalid", "Prescription form does not have correct format" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "PrescriptionForm", form );
            return "hcp/addPrescription";
        }
        else {
            script.save();
            return "hcp/addPrescriptionResult";
        }
    }

}
