package edu.ncsu.csc.itrust2.controllers.admin;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.forms.admin.DeleteICDCodeForm;
import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;

/**
 * Class that enables an Admin to add an ICDCode to the system.
 *
 * @author Felix Kim
 *
 */
@Controller
public class ICDCodeController {

    /**
     * Creates the form page for the Add ICDCode page
     *
     * @param model
     *            Data for the front end
     * @return Page to show to the user
     */
    @RequestMapping ( value = "admin/addICDCode" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addICDCode ( final Model model ) {
        model.addAttribute( "ICDCodeForm", new ICDCodeForm() );
        return "/admin/addICDCode";
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
    @PostMapping ( "/admin/addICDCode" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addICDCodeSubmit ( @Valid @ModelAttribute ( "ICDCodeForm" ) final ICDCodeForm form,
            final BindingResult result, final Model model ) {
        ICDCode icd = null;
        try {
            icd = new ICDCode( form );
            if ( ICDCode.getByCode( icd.getCode() ) != null ) {
                result.rejectValue( "code", "code.notvalid", "This ICD code already exists" );
            }
        }
        catch ( final Exception e ) {
        		result.rejectValue( "code", "code.notvalid", "ICD code does not have correct format" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "ICDCodeForm", form );
            return "/admin/addICDCode";
        }
        else {
            icd.save();
            return "admin/addICDCodeResult";
        }
    }

    /**
     * Displays the form for an Admin to delete an ICDCode from the system
     *
     * @param model
     *            Data for the front end
     * @return The page to display to the user
     */
    @RequestMapping ( value = "admin/deleteICDCode" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteICDCode ( final Model model ) {
        model.addAttribute( "icdCodes", ICDCode.getAll() );
        return "admin/deleteICDCode";
    }

    /**
     * Processes the form for an Admin to delete an ICDCode from the system
     *
     * @param form
     *            DeleteICDCodeForm to use to delete the ICD code
     * @param result
     *            Validation result
     * @param model
     *            Data from the front end
     * @return Page to show to the user
     */
    @PostMapping ( "/admin/deleteICDCode" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteICDCodeSubmit ( @Valid @ModelAttribute ( "DeleteICDCodeForm" ) final DeleteICDCodeForm form,
            final BindingResult result, final Model model ) {
        final ICDCode icd = ICDCode.getByCode( form.getCode() );
        if ( null != form.getConfirm() && null != icd ) {
            icd.delete();
            return "admin/deleteICDCodeResult";
        }
        else if ( null == icd ) {
            result.rejectValue( "code", "code.notvalid", "ICD code cannot be found" );
        }
        else {
            result.rejectValue( "confirm", "confirm.notvalid", "You must confirm that the ICD code should be deleted" );
        }
        return "admin/deleteICDCode";

    }

}