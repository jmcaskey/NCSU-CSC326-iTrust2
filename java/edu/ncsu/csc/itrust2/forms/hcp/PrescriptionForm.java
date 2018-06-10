package edu.ncsu.csc.itrust2.forms.hcp;

/**
 * Prescription form used to document a prescription by the HCP. Validated PrescriptionForms
 * will be stored as Prescriptions within the database.
 * @author Ray Black
 *
 */
public class PrescriptionForm {
	/** The ID of the office visit this prescription is associated with, or -1 if no such visit exists */
	private Long officeVisitId;
	/** A string representation of the ndc code of this prescription */
	private String ndc;
	/** A string representation of the patient this prescription belongs to*/
	private String patient;
	/** A string representation of the dosage of this prescription */
	private String dosage;
	/** A string representation of the start date of this prescription */
	private String startDate;
	/** A string representation of the end date of this prescription */
	private String endDate;
	/** A string representation of the number of renewals this prescription has */
	private String numRenewals;
	
	/**
     * Empty constructor so that we can create an Prescription form for the user
     * to fill out
     */
    public PrescriptionForm () {
    }
    
    /**
     * Retrieve the id of the office visit ID prescription form is associated with
	 * @return the id of the office visit
	 */
	public Long getOfficeVisitId() {
		return officeVisitId;
	}

	/**
	 * Set the id of the office visit ID prescription form is associated with
	 * @param id the id of the office visit to associate this prescription with
	 */
	public void setId(Long id) {
		this.officeVisitId = id;
	}

	/**
	 * Retrieve the ndc drug code of this form
	 * @return the ndc
	 */
	public String getNdc() {
		return ndc;
	}

	/**
	 * Sets the ndc drug code of this form
	 * @param ndc the ndc code to set
	 */
	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	/**
	 * Retrieve the patient of this form
	 * @return the patient
	 */
	public String getPatient() {
		return patient;
	}

	/**
	 * Sets the patient of this form
	 * @param patient the patient to set
	 */
	public void setPatient(String patient) {
		this.patient = patient;
	}

	/**
	 * Retrieves the dosage of this form
	 * @return the dosage
	 */
	public String getDosage() {
		return dosage;
	}

	/**
	 * Sets the dosage of this form
	 * @param dosage the dosage to set
	 */
	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	/**
	 * Retrieves the start date of this form
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date of this form
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * Retrieves the end date of this form
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date of this form
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * Retrieves the number of renewals of this form
	 * @return the numRenewals
	 */
	public String getNumRenewals() {
		return numRenewals;
	}

	/**
	 * Sets the number of renewals of this form
	 * @param numRenewals the numRenewals to set
	 */
	public void setNumRenewals(String numRenewals) {
		this.numRenewals = numRenewals;
	}

    
    
}
