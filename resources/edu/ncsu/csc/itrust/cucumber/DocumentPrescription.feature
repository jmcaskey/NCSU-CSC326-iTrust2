#Author jmcaskey

Feature: Document prescriptions
	As an iTrust2 HCP and Admin
	I want to document prescriptions 
	So that a record exits of a prescription being prescribed

Scenario Outline: Document an office visit with prescription
Given The required entries exist for prescription: <prescription>
And A patient exists to prescribe with name: <first> <last> and date of birth: <birthday>
When I login to iTrust2 as the HCP for prescribing
When I fill in health metrics on the office visit for infants
And I select the filled prescription: <prescription>, start date: <start>, end date: <end>, dosage: <dosage>, and renewals: <renewals>
Then The prescription is documented in office visit successfully

Examples:
	| first   | last       | birthday   | prescription   | start      | end        | dosage | renewals |
	| Ivan    | Ivanovich  | 09/29/2017 | Asparagus      | 11/12/2017 | 12/12/2017 | 100    | 2        |
	
Scenario Outline: Document a prescription without office visit
Given The required entries exist for prescription: <prescription>
And A patient exists to prescribe with name: <first> <last> and date of birth: <birthday>
When I login to iTrust2 as the HCP for prescribing
When I navigate to the Document Prescription page
And I select the filled prescription: <prescription>, start date: <start>, end date: <end>, dosage: <dosage>, and renewals: <renewals>
Then The prescription is documented successfully

Examples:
	| first    | last       | birthday   | prescription      | start      | end        | dosage | renewals |
	|  Ivan    | Ivanovich  | 09/29/2017 | Jack Pine Pollen  | 11/12/2017 | 12/12/2017 | 100    | 24       |
	
Scenario Outline: Add prescriptions to list for hcp
When I log in to iTrust2 as a Admin for prescribing
When I navigate to the Add NDC page
And I select the filled prescription: <prescription> and NDC code: <code> 
Then The prescription is add successfully

Examples:
	| prescription  | code         |
	| careless weed | 4988-0025-50 |

