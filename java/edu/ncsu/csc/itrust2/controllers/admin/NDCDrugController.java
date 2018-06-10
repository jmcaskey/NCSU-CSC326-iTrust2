package edu.ncsu.csc.itrust2.controllers.admin;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.forms.admin.DeleteNDCDrugForm;
import edu.ncsu.csc.itrust2.forms.admin.NDCDrugForm;
import edu.ncsu.csc.itrust2.models.persistent.NDCDrug;

/**
 * Class that enables an Admin to add an NDCDrug to the system.
 *
 * @author Felix Kim
 *
 */
@Controller
public class NDCDrugController {

    /**
     * Creates the form page for the Add NDCDrug page
     *
     * @param model
     *            Data for the front end
     * @return Page to show to the user
     */
    @RequestMapping ( value = "admin/addNDCDrug" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addNDCDrug ( final Model model ) {
        model.addAttribute( "NDCDrugForm", new NDCDrugForm() );
        return "/admin/addNDCDrug";
    }

    /**
     * Parses the NDCDrugForm from the User
     *
     * @param form
     *            NDCDrugForm to validate and save
     * @param result
     *            Validation result
     * @param model
     *            Data from the front end
     * @return The page to show to the user
     */
    @PostMapping ( "/admin/addNDCDrug" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addNDCDrugSubmit ( @Valid @ModelAttribute ( "NDCDrugForm" ) final NDCDrugForm form,
            final BindingResult result, final Model model ) {
        NDCDrug ndc = null;
        try {
            ndc = new NDCDrug( form );
            if ( NDCDrug.getByCode( ndc.getCode() ) != null ) {
                result.rejectValue( "code", "code.notvalid", "This NDC already exists" );
            }
        }
        catch ( final Exception e ) {
        		result.rejectValue( "code", "code.notvalid", "NDC does not have correct format" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "NDCDrugForm", form );
            return "/admin/addNDCDrug";
        }
        else {
            ndc.save();
            return "admin/addNDCDrugResult";
        }
    }

    /**
     * Displays the form for an Admin to delete an NDCDrug from the system
     *
     * @param model
     *            Data for the front end
     * @return The page to display to the user
     */
    @RequestMapping ( value = "admin/deleteNDCDrug" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteNDCDrug ( final Model model ) {
        model.addAttribute( "ndcDrugs", NDCDrug.getAll() );
        return "admin/deleteNDCDrug";
    }

    /**
     * Processes the form for an Admin to delete an NDCDrug from the system
     *
     * @param form
     *            DeleteNDCDrugForm to use to delete the NDCDrug
     * @param result
     *            Validation result
     * @param model
     *            Data from the front end
     * @return Page to show to the user
     */
    @PostMapping ( "/admin/deleteNDCDrug" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteNDCDrugSubmit ( @Valid @ModelAttribute ( "DeleteNDCDrugForm" ) final DeleteNDCDrugForm form,
            final BindingResult result, final Model model ) {
        final NDCDrug ndc = NDCDrug.getByCode( form.getCode() );
        if ( null != form.getConfirm() && null != ndc ) {
            ndc.delete();
            return "admin/deleteNDCDrugResult";
        }
        else if ( null == ndc ) {
            result.rejectValue( "code", "code.notvalid", "NDC cannot be found" );
        }
        else {
            result.rejectValue( "confirm", "confirm.notvalid", "You must confirm that the NDC should be deleted" );
        }
        return "admin/deleteNDCDrug";

    }

}