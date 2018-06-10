#Author jmcaskey
Feature: Edit patient demographics
	As a HCP
	I want to edit a patient's demographics
	So that the information is stored and patients can see the new information

Scenario: Edit patient demographics
Given A patient exists in the system
When I log in to iTrust2 as the HCP
When I navigate to the Office Visit page to edit demographics
When I select a patient and edit their demographics
When I fill in new, updated, patient demographics
Then The patient demographics are updated
And The new patient demographics can be viewed