package edu.ncsu.csc.itrust2.models.persistent;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.utils.DomainObjectCache;

/**
 * This is the validated database-persisted ICD Code representation
 *
 * @author Felix Kim
 *
 */
@Entity
@Table ( name = "ICDCode" )
public class ICDCode extends DomainObject<ICDCode> {

    /**
     * The cache representation of the ICD Codes in the database
     */
    private static DomainObjectCache<String, ICDCode> cache = new DomainObjectCache<String, ICDCode>(
            ICDCode.class );
    
    /**
     * Get a specific ICDCode object by the string representation of the ICD code
     *
     * @param code
     *            the string representation of the ICD code
     * @return the specific ICDCode object with the matching string representation of the code
     */
    public static ICDCode getByCode ( final String code ) {
        
        ICDCode icd = cache.get( code );
        if ( null == icd ) {
            try {
                icd = getWhere( " code = '" + code + "'" ).get( 0 );
                cache.put( code, icd );
            }
            catch ( final Exception e ) {
                // Exception ignored
            }
        }
        return icd;
    }

    /**
     * Get all ICD codes in the database
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<ICDCode> Because get all just
     *                   returns a list of ICDCodes, the cast is okay.
     *
     * @return all ICD codes in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<ICDCode> getAll () {
        final List<ICDCode> codes = (List<ICDCode>) getAll( ICDCode.class );
        codes.sort( new Comparator<ICDCode>() {
            @Override
            public int compare ( final ICDCode c1, final ICDCode c2 ) {
                return c1.getCode().compareTo( c2.getCode() );
            }
        } );
        System.err.println( codes );
        return codes;
    }

    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<ICDCode> Because getWhere just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            The specific query on the database
     * @return the result of the query
     */
    @SuppressWarnings ( "unchecked" )
    public static List<ICDCode> getWhere ( final String where ) {
        return (List<ICDCode>) getWhere( ICDCode.class, where );
    }

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public ICDCode () {
    }

    /**
     * Creates an ICDCode from the ICDCodeForm provided
     *
     * @param icdf
     *            ICDCodeForm The ICDCodeForm to create an ICDCode
     *            out of
     * @throws NumberFormatException
     *             If the ID cannot be parsed to a Long.
     */
    public ICDCode ( final ICDCodeForm icdf ) throws NumberFormatException {
        
        setName(icdf.getName());
        setCode(icdf.getCode());

        if ( icdf.getId() != null ) {
            setId( Long.parseLong( icdf.getId() ) );
        }
    }

    /**
     * Get the code for this ICDCode
     *
     * @return the code of this ICDCode
     */
    public String getCode () {
        return code;
    }

    /**
     * Set the code for this ICDCode.
     * Need to perform back-end validation to make sure code has correct format:
     * one uppercase letter | two digits | decimal point | 0-2 digits
     * An example of a code with the correct format is: E78.0
     *
     * @param theCode
     *            the string representation of the code to set
     */
    public void setCode ( final String theCode ) {
    		// Make sure that the passed in code has the correct ICD format
    		final String icdCodeRegEx = "[A-Z][0-9][0-9]\\.[0-9]{0,2}";
    		boolean isValidICD = Pattern.matches(icdCodeRegEx, theCode);
    		if (!isValidICD) {
    			throw new IllegalArgumentException("The ICD code is not of the correct format.");
    		}
    	
        code = theCode;
    }

    /**
     * Get the id of this ICDCode
     *
     * @return the id of this ICDCode
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the id of this ICDCode
     *
     * @param id
     *            the id to set this ICDCode to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the name of diagnosis corresponding to the ICDCode
     *
     * @return the name of diagnosis corresponding to the ICDCode
     */
    public String getName () {
        return name;
    }

    /**
     * Set the name of diagnosis corresponding to the ICDCode
     *
     * @param theName
     *            the name to set this ICDCode to
     */
    public void setName ( final String theName ) {
        name = theName;
    }
    
    /**
     * The string representation of the ICD code, e.g. E78.0
     */
    @NotNull
    private String code;
    
    /**
     * The name of the diagnosis
     */
    @NotNull
    private String name;

    /**
     * The id of this ICDCode
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long               id;
}
