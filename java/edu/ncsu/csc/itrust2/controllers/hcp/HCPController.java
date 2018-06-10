package edu.ncsu.csc.itrust2.controllers.hcp;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller class responsible for managing the behavior for the HCP Landing
 * Screen
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class HCPController {

    /**
     * Returns the Landing screen for the HCP
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "hcp/index" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_HCP.getLanding();
    }

    /**
     * Returns the form page for an HCP to add a standalone prescription
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/hcp/addPrescription" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String viewDiagnoses ( final Model model ) {
        return "/hcp/addPrescription";
    }

    /**
     * Provides the page for a HCP to view and edit patient demographics
     * @param username the Username of the patient whose demographics will be edited
     * @param model
     *            The data for the front end
     * @return The page to show the user so they can edit demographics
     */
    @GetMapping ( value = "hcp/editDemographics/{username}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String viewDemographics ( @PathVariable ( "username" ) final String username, final Model model ) {

        final User self = User.getByName( username );
        final PatientForm form = new PatientForm( Patient.getPatient( self ) );
        model.addAttribute( "PatientForm", form );
        LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, self );

        return "/hcp/editDemographics";
    }

    /**
     *
     * Provides the page for a User to edit their password
     *
     * @param model
     *            The data for the front end
     * @return The page for the user to edit their password
     */
    @GetMapping ( value = "hcp/editPassword" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String viewPassword ( final Model model ) {
        final User self = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
        final UserForm form = new UserForm( self );
        model.addAttribute( "UserForm", form );
        // LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, self );
        return "/hcp/editPassword";
    }

    /**
     * Edit user password
     *
     * @param form
     *            form information
     * @param result
     *            result
     * @param model
     *            data for front end
     * @return mapping
     */
    @PostMapping ( "hcp/editPassword" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String editPasswordSubmit ( @Valid @ModelAttribute ( "UserForm" ) final UserForm form,
            final BindingResult result, final Model model ) {
        User u = null;
        try {
            u = new User( form );
            // if ( User.getByName( u.getUsername() ) != null ) {
            // result.rejectValue( "username", "username.notvalid", "Username is
            // already in use" );
            // }
        }
        catch ( final Exception e ) {
            result.rejectValue( "password", "password.notvalid", "Passwords invalid or do not match" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "UserForm", form );
            return "/hcp/editPassword";
        }
        else {
            u.save();
            switch ( u.getRole() ) {
                // user is created, lets create the specific table entries
                case ROLE_ADMIN:
                case ROLE_HCP:
                    final Personnel per = new Personnel();
                    per.setSelf( u );
                    per.save();
                    break;
                case ROLE_PATIENT:
                    final Patient pat = new Patient();
                    pat.setSelf( u );
                    pat.save();
                    break;
                default:
                    // shouldn't reach
                    break;
            }
             LoggerUtil.log( TransactionType.PASSWORD_UPDATE,
             SecurityContextHolder.getContext().getAuthentication().getName() );
            return "hcp/editPasswordResult";
        }
    }

}
