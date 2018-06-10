package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form used for an Admin to delete an NDCDrug stored in the system
 *
 * @author Felix Kim
 *
 */
public class DeleteNDCDrugForm {
    
    /**
     * Code of the NDCDrug to delete
     */
    private String code;

    /**
     * Whether the user selected to confirm their action
     */
    @NotEmpty
    private String confirm;

    /**
     * Retrieve the code from the form
     *
     * @return The code of the NDCDrug
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the code of the NDCDrug to delete
     *
     * @param code
     *            The code of the NDCDrug to set
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Retrieve whether the user confirmed the delete action
     *
     * @return Confirmation
     */
    public String getConfirm () {
        return confirm;
    }

    /**
     * Whether or not the user confirmed the action
     *
     * @param confirm
     *            Confirmation
     */
    public void setConfirm ( final String confirm ) {
        this.confirm = confirm;
    }
}