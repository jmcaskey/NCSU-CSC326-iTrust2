'use strict';
var app = angular.module('myApp', []);
app.controller('addPrescriptionCtrl', function ($scope, $http) {
  var self = this;

  self.prescription = makePrescriptionObject();

  $http.get("/iTrust2/api/v1/patients").then(
    function (response) {
      self.prescription.patient.options = response.data;
      self.prescription.patient.selected = self.prescription.patient.options[0];
      self.prescription.patient.verify();
    });

  $http.get("/iTrust2/api/v1/ndcDrugs").then(
    function (response) {
      self.prescription.ndc.options = response.data;
      for (var i = 0; i < self.prescription.ndc.options.length; i++) {
        var ndc = self.prescription.ndc.options[i];
        ndc.label = ndc.code + " - " + ndc.name;
      }
      self.prescription.ndc.selected = self.prescription.ndc.options[0];
      self.prescription.ndc.verify();
    });

  window.s = self;

  self.submit = function () {
    self.successMessage = "";
    self.failureMessage = "";
    var errorArr = [];


    var errorArr = [];
    var prescriptionFieldsToSend = ["startDate", "endDate", "dosage", "renewals"];
    for (var i = 0; i < prescriptionFieldsToSend.length; i++) {
      var field = prescriptionFieldsToSend[i];
      if (self.prescription[field].invalid) {
        errorArr.push(self.prescription[field].name + " field is invalid.");
      }
    }
    if (self.prescription.dateRange.invalid) {
      errorArr.push("Prescription date range is invalid.");
    }

    if (errorArr.length > 0) {
      $scope.message = "";
      $scope.errorMsg = "Error: " + errorArr.join(" ");
      return;
    }

    //all error checking done
    var infoToSend = {};

    var infoToSend = {};
    infoToSend.patient = self.prescription.patient.selected.self.username;
    infoToSend.officeVisitId = -1;
    infoToSend.ndc = self.prescription.ndc.value;
    infoToSend.startDate = self.prescription.startDate.value
    infoToSend.endDate = self.prescription.endDate.value
    infoToSend.dosage = self.prescription.dosage.value;
    infoToSend.numRenewals = self.prescription.renewals.value;

    console.log(infoToSend);
    console.log(JSON.stringify(infoToSend));

    var httpRequest = {
      method: 'POST',
      url: '/iTrust2/api/v1/prescriptions',
      data: infoToSend,
    };

    $http(httpRequest).then(function (response) {
      $scope.message = "Prescription created successfully";
      $scope.errorMsg = "";
      console.log(response);
    }, function (rejection) {
      $scope.message = "";
      $scope.errorMsg = "Error occurred creating prescription";
      console.log(rejection);
    })

  }
});