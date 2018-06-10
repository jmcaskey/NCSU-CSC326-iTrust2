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

import edu.ncsu.csc.itrust2.forms.admin.NDCDrugForm;
import edu.ncsu.csc.itrust2.utils.DomainObjectCache;

/**
 * This is the validated database-persisted NDC representation
 *
 * @author Felix Kim
 *
 */
@Entity
@Table ( name = "NDCDrug" )
public class NDCDrug extends DomainObject<NDCDrug> {

    /**
     * The cache representation of the NDC's in the database
     */
    private static DomainObjectCache<String, NDCDrug> cache = new DomainObjectCache<String, NDCDrug>(
            NDCDrug.class );
    
    /**
     * Get a specific NDCDrug object by the string representation of the NDCDrug
     *
     * @param code
     *            the string representation of the NDCDrug
     * @return the specific NDCDrug object with the matching string representation of the code
     */
    public static NDCDrug getByCode ( final String code ) {
        
        NDCDrug ndc = cache.get( code );
        if ( null == ndc ) {
            try {
                ndc = getWhere( " code = '" + code + "'" ).get( 0 );
                cache.put( code, ndc );
            }
            catch ( final Exception e ) {
                // Exception ignored
            }
        }
        return ndc;
    }

    /**
     * Get all NDC's in the database
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<NDCDrug> Because get all just
     *                   returns a list of NDCDrugs, the cast is okay.
     *
     * @return all NDC's in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<NDCDrug> getAll () {
        final List<NDCDrug> codes = (List<NDCDrug>) getAll( NDCDrug.class );
        codes.sort( new Comparator<NDCDrug>() {
            @Override
            public int compare ( final NDCDrug ndc1, final NDCDrug ndc2 ) {
                return ndc1.getCode().compareTo( ndc2.getCode() );
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
     *                   DomainObject> to List<NDCDrug> Because getWhere just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            The specific query on the database
     * @return the result of the query
     */
    @SuppressWarnings ( "unchecked" )
    public static List<NDCDrug> getWhere ( final String where ) {
        return (List<NDCDrug>) getWhere( NDCDrug.class, where );
    }

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public NDCDrug () {
    }

    /**
     * Creates an NDCDrug from the NDCDrugForm provided
     *
     * @param ndcf
     *            NDCDrugForm The NDCDrugForm to create an NDCDrug
     *            out of
     * @throws NumberFormatException
     *             If the ID cannot be parsed to a Long.
     */
    public NDCDrug ( final NDCDrugForm ndcf ) throws NumberFormatException {
        
        setName(ndcf.getName());
        setCode(ndcf.getCode());

        if ( ndcf.getId() != null ) {
            setId( Long.parseLong( ndcf.getId() ) );
        }
    }

    /**
     * Get the code for this NDCDrug
     *
     * @return the code of this NDCDrug
     */
    public String getCode () {
        return code;
    }

    /**
     * Set the code for this NDCDrug.
     * Need to perform back-end validation to make sure NDC has correct format:
     * 4 digits (0-9) | dash (-) | 4 digits (0-9) | dash (-) | 2 digits (0-9)  
     * An example of a code with the correct format is: 0777-3105-02
     *
     * @param theCode
     *            the string representation of the code to set!
     */
    public void setCode ( final String theCode ) {
    		// Make sure that the passed in code has the correct NDC format
    		final String ndcCodeRegEx = "[0-9]{4}-[0-9]{4}-[0-9]{2}";
    		boolean isValidNDC = Pattern.matches(ndcCodeRegEx, theCode);
    		if (!isValidNDC) {
    			throw new IllegalArgumentException("The NDC is not of the correct format.");
    		}
    	
        code = theCode;
    }

    /**
     * Get the id of this NDC
     *
     * @return the id of this NDC
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the id of this NDC
     *
     * @param id
     *            the id to set this NDC to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the drug name corresponding to the NDC (e.g. "Prozac")
     *
     * @return the name of drug corresponding to the NDC
     */
    public String getName () {
        return name;
    }

    /**
     * Set the name of drug corresponding to the NDC
     *
     * @param theName
     *            the name to set this NDC to
     */
    public void setName ( final String theName ) {
        name = theName;
    }
    
    /**
     * The string representation of the NDC code, e.g. 0777-3105-02
     */
    @NotNull
    private String code;
    
    /**
     * The name of the drug
     */
    @NotNull
    private String name;

    /**
     * The id of this NDC
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long               id;
}

