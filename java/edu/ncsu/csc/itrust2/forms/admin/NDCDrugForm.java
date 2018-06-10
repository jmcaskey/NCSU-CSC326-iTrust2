package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.persistent.NDCDrug;

/**
 * Form used for adding a new NDCDrug to the system. Will be parsed into an actual NDCDrug
 * object to be saved.
 *
 * @author Felix Kim
 *
 */
public class NDCDrugForm {

    /**
     * The string representation of the NDCDrug code
     */
    @NotEmpty
    private String code;

    /**
     * The name of the corresponding drug
     */
    @NotEmpty
    private String name;
    
    /**
     * ID of the NDCDrug
     */
    private String                 id;

    /**
     * Creates an empty NDCDrugForm object. Used by the controllers for filling
     * out a new NDCDrugForm.
     */
    public NDCDrugForm () {
    }

    /**
     * Creates an NDCDrugForm from the NDCDrug provided. Used to convert a
     * NDCDrug to a form that can be edited.
     *
     * @param ndc
     *            NDCDrug to convert to its Form.
     */
    public NDCDrugForm ( final NDCDrug ndc ) {
        setName( ndc.getName() );
        setCode( ndc.getCode());
        setId(ndc.getId().toString());
    }

    /**
     * Gets the drug name of the NDC from this NDCDrugForm
     *
     * @return Drug name of the NDCDrug
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the drug name of the NDCDrug in this NDCDrugForm
     *
     * @param name
     *            New drug name of the NDCDrug
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the code of the NDCDrug in this NDCDrugForm.
     *
     * @return The code of the NDCDrug
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the code of this NDCDrug.
     *
     * @param code
     *            New code to set.
     */
    public void setCode ( final String code ) {
        this.code = code;
    }
    
    /**
     * Gets the ID of the NDCDrug
     *
     * @return ID of the NDCDrug
     */
    public String getId () {
        return this.id;
    }

    /**
     * Sets the ID of the NDCDrug
     *
     * @param id
     *            The ID of the NDCDrug
     */
    public void setId ( final String id ) {
        this.id = id;
    }

}
