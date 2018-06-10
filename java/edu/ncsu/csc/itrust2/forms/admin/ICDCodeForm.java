package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.persistent.ICDCode;

/**
 * Form used for adding a new ICDCode to the system. Will be parsed into an actual ICDCode
 * object to be saved.
 *
 * @author Felix Kim
 *
 */
public class ICDCodeForm {

    /**
     * The string representation of the ICD Code
     */
    @NotEmpty
    @Length ( max = 6 )
    private String code;

    /**
     * The name of the corresponding diagnosis
     */
    @NotEmpty
    private String name;
    
    /**
     * ID of the ICDCode
     */
    private String                 id;

    /**
     * Creates an empty ICDCodeForm object. Used by the controllers for filling
     * out a new ICDCode.
     */
    public ICDCodeForm () {
    }

    /**
     * Creates an ICDCodeForm from the ICDCode provided. Used to convert a
     * ICDCode to a form that can be edited.
     *
     * @param icd
     *            ICDCode to convert to its Form.
     */
    public ICDCodeForm ( final ICDCode icd ) {
        setName( icd.getName() );
        setCode( icd.getCode());
        setId(icd.getId().toString());
    }

    /**
     * Gets the name of the ICDCode from this ICDCodeForm
     *
     * @return Name of the ICDCode
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of the ICDCode in this ICDCodeForm
     *
     * @param name
     *            New Name of the ICDCode
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the code of the ICDCode in this ICDCodeForm.
     *
     * @return The code of the ICDCode
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the code of this ICDCode.
     *
     * @param code
     *            New code to set.
     */
    public void setCode ( final String code ) {
        this.code = code;
    }
    
    /**
     * Gets the ID of the ICDCode
     *
     * @return ID of the ICDCode
     */
    public String getId () {
        return this.id;
    }

    /**
     * Sets the ID of the ICDCode
     *
     * @param id
     *            The ID of the ICDCode
     */
    public void setId ( final String id ) {
        this.id = id;
    }

}
