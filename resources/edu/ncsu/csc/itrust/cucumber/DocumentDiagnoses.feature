#Author jmcaskey
Feature: Diagnose a patient during an office visit
	As an iTrust HCP
	I want to document a diagnosis in an office visit
	so that a record exists of the diagnosis assigned to a Patient
	
Scenario Outline: Document an office visit with diagnoses for infant
Given The required entries exist for diagnosis
And A patient exists to diagnose with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for infants
And I fill in information on the diagnosis with diagnosis: <diagnosis>, and  ICD-10 id: <id>
Then The diagnosis is documented successfully
And The diagnosis for the office visit is correct

Examples:
	| first    | last    | birthday   | diagnosis     | id    |
	| Caldwell | Hudson  | 09/29/2017 | Careless Weed | E00.1 |
	
Scenario Outline: Document an office visit with diagnoses for child
Given The required entries exist for diagnosis
And A patient exists to diagnose with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for children
And I fill in information on the diagnosis with diagnosis: <diagnosis>, and  ICD-10 id: <id>
Then The diagnosis is documented successfully
And The diagnosis for the office visit is correct

Examples:
	| first    | last    | birthday   | diagnosis     | id    |
	| Caldwell | Hudson  | 09/29/2008 | Careless Weed | E00.1 |
	
	
Scenario Outline: Document diagnoses for adult with an office visit
Given The required entries exist for diagnosis
And A patient exists to diagnose with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for adults
And I fill in information on the diagnosis with diagnosis: <diagnosis>, and  ICD-10 id: <id>
Then The diagnosis is documented successfully
And The diagnosis for the office visit is correct

Examples:
	| first    | last    | birthday   | diagnosis     | id    |
	| Caldwell | Hudson  | 09/29/1971 | Careless Weed | E00.1 |

	