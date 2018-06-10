package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.utils.DomainObjectCache;

/**
 * Validated data-base representation of prescriptions.
 * @author Ray Black
 */
@Entity
@Table ( name = "Prescription" )
public class Prescription extends DomainObject<Prescription> {
	
    /**
     * The cache representation of the prescriptions in the database
     */
    private static DomainObjectCache<Long, Prescription> cache = new DomainObjectCache<Long, Prescription>(
            Prescription.class );
    
    /**
     * Get a specific prescription by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific prescription with the desired ID
     */
    public static Prescription getById ( final Long id ) {
        Prescription visit = cache.get( id );
        if ( null == visit ) {
            try {
                visit = getWhere( "id = '" + id + "'" ).get( 0 );
                cache.put( id, visit );
            }
            catch ( final Exception e ) {
                // Exception ignored
            }
        }
        return visit;
    }
    
    /**
     * Returns all of the prescriptions in the database.
     * 
     * @SuppressWarnings As per the documentation in DomainObject.java, this
     *                   is a necessary cast. The getAll call should return
     *                   a list of only prescriptions anyway.
     * 
     * @return a List of all prescriptions in the database
     */
    @SuppressWarnings("unchecked")
	public static List<Prescription> getAll () {
        final List<Prescription> codes = (List<Prescription>) getAll( Prescription.class );
        codes.sort( new Comparator<Prescription>() {
            @Override
            public int compare ( final Prescription c1, final Prescription c2 ) {
                return c1.getId().compareTo( c2.getId() );
            }
        } );
        System.err.println( codes );
        return codes;
    }
    
    /**
     * Get all prescriptions for a specific patient
     *
     * @param patientName
     *            the name of the patient
     * @return the prescriptions of the queried patient
     */
    public static List<Prescription> getForPatient ( final String patientName ) {
        return getWhere( " patient_id = '" + patientName + "'" );
    }
    
    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<Prescription> Because getWhere just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            The specific query on the database
     * @return the result of the query
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Prescription> getWhere ( final String where ) {
    		return (List<Prescription>) getWhere( Prescription.class, where );
    }

    
    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public Prescription() {
    }
    
    /**
     * Constructs a new prescription from a prescription form, performing
     * validation where necessary
     * @param pf the prescription form to construct a prescription from
     * @throws ParseException If the start or end date cannot be parsed properly
     */
    public Prescription(PrescriptionForm pf) throws ParseException {
    		setPatient( User.getByNameAndRole( pf.getPatient(), Role.ROLE_PATIENT ) );
    		setDosage(Integer.parseInt(pf.getDosage()));
    		setNumRenewals(Integer.parseInt(pf.getNumRenewals()));
    		
    		final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
    		final Date parsedStartDate = sdf.parse( pf.getStartDate());
    		final Calendar cStart = Calendar.getInstance();
    		cStart.setTime( parsedStartDate );
    		final Date parsedEndDate = sdf.parse( pf.getEndDate());
    		final Calendar cEnd = Calendar.getInstance();
    		cEnd.setTime( parsedEndDate );
        
    		setEndDate(cEnd);
    		setStartDate( cStart );
    		setNdc(NDCDrug.getByCode(pf.getNdc()));
    		if(ndc == null)
    			throw new IllegalArgumentException(); //Code not found
    	
        if ( pf.getOfficeVisitId() != -1 ) { // If we have an office visit to use
            setOfficeVisit(OfficeVisit.getById(pf.getOfficeVisitId())); 
        }
    }

	/**
     * The id of this prescription. Acts as primary key
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
	private Long id;
	
    /**
     * The National Drug Code associated with this prescription
     */
    @NotNull
    @ManyToOne
    @JoinColumn (name = "NDCCode")
	private NDCDrug ndc;
	
    /**
     * The patient this prescription belongs to
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id" )
	private User patient;
	
    /**
     * The dosage of this prescription
     */
    @NotNull
    @Min(1)
	private Integer dosage;
	
    /**
     * The start date for this prescription
     */
    @NotNull
	private Calendar startDate;
	
    /**
     * The end date for this prescription
     */
    @NotNull
	private Calendar endDate;
	
    /**
     * The number of renewals this prescription has
     */
    @NotNull
    @Min(0)
	private Integer numRenewals;
    
    /**
     * The office visit this prescription is associated with, if one exists
     */
    @OneToOne
    @JoinColumn(name = "OfficeVisits")
    private OfficeVisit visit;

	/**
	 * Returns the ID of this prescription
	 * @return The ID of this prescription
	 */
    @Override
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID of this prescription
	 * @param id the ID to set this prescription to
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the NDC code of this prescription
	 * @return the NDC drug code of this prescription
	 */
	public NDCDrug getNdc() {
		return ndc;
	}

	/**
	 * Sets the NDC code of this prescription
	 * @param ndc the NDC drug code to set this prescription to
	 */
	public void setNdc(NDCDrug ndc) {
		this.ndc = ndc;
	}

	/**
	 * Returns the patient this prescription belongs to
	 * @return the patient of this prescription
	 */
	public User getPatient() {
		return patient;
	}

	/**
	 * Sets the patient of this prescription
	 * @param patient the patient to assign to this prescription
	 */
	public void setPatient(User patient) {
		this.patient = patient;
	}

	/**
	 * Returns the dosage of this prescription
	 * @return the dosage of this prescription
	 */
	public Integer getDosage() {
		return dosage;
	}

	/**
	 * Sets the dosage of this prescription
	 * @param dosage the dosage to set this prescription to
	 */
	public void setDosage(Integer dosage) {
		this.dosage = dosage;
	}

	/**
	 * Returns the start date of this prescription
	 * @return the start date of this prescription
	 */
	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date of this prescription
	 * @param startDate the date to set this prescription's start date to
	 */
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	/**
	 * Returns the end date of this prescription
	 * @return the end date of this prescription
	 */
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date of this prescription
	 * @param endDate the date to set this prescription's end date to
	 */
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	/**
	 * Returns the number of renewals this prescription has
	 * @return the number of renewals this prescription has
	 */
	public Integer getNumRenewals() {
		return numRenewals;
	}

	/**
	 * Sets the number of renewals this prescription has
	 * @param numRenewals the number of renewals to set this prescription to
	 */
	public void setNumRenewals(Integer numRenewals) {
		this.numRenewals = numRenewals;
	}
	
	/**
	 * Sets the office visit associated with this prescription
	 * @param ov the OfficeVisit to associate this prescription with
	 */
    private void setOfficeVisit(OfficeVisit ov) {
		this.visit = ov;
	}
    
    /**
     * Returns the office visit associated with this prescription
     * @return the office visit associated with this prescription
     */
    public OfficeVisit getOfficeVisit() {
    		return this.visit;
    }

}
