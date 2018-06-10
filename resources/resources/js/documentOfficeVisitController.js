'use strict';
var app = angular.module('myApp', []);
app.controller('documentOfficeVisitCtrl', function ($scope, $http) {
  var self = this;
  $scope.selected = false;
  $scope.three = false;
  $scope.threeAndUp = false;
  $scope.twelveAndUp = false;

  //calls makeVisitObject declared in visitObject.js, which returns a new instance of a visit object
  self.visit = makeVisitObject();
  self.prescription = makePrescriptionObject();

  $http.get("/iTrust2/api/v1/patients").then(
    function (response) {
      self.visit.patient.patientList = response.data;
    });
  $http.get("/iTrust2/api/v1/appointmenttype")
    .then(function (response) {
      self.visit.type.typeList = response.data;
    });
  $http.get("/iTrust2/api/v1/housesmoking")
    .then(function (response) {
      self.visit.houseSmokingStatus.options = response.data;
    });
  $http.get("/iTrust2/api/v1/patientsmoking")
    .then(function (response) {
      self.visit.patientSmokingStatus.options = response.data;
    });
  $http.get("/iTrust2/api/v1/hospitals").then(
    function (response) {
      self.visit.hospital.hospitalList = response.data;
    });

  $http.get("/iTrust2/api/v1/icdCodes").then(
    function (response) {
      self.visit.icdCode.options = response.data;
      for (var i = 0; i < self.visit.icdCode.options.length; i++) {
        var icd = self.visit.icdCode.options[i];
        icd.label = icd.code + " - " + icd.name;
      }
      var blankOpt = { name: "", code: "", label: "<none>" };
      self.visit.icdCode.options.unshift(blankOpt);
      self.visit.icdCode.selected = blankOpt;
    });

  $http.get("/iTrust2/api/v1/ndcDrugs").then(
    function (response) {
      self.prescription.ndc.options = response.data;
      for (var i = 0; i < self.prescription.ndc.options.length; i++) {
        var ndc = self.prescription.ndc.options[i];
        ndc.label = ndc.code + " - " + ndc.name;
      }
      var blankOpt = { name: "", code: "", label: "<none>" };
      self.prescription.ndc.options.unshift(blankOpt);
      self.prescription.ndc.selected = blankOpt;
    });

  window.s = self;

  self.showMe = function () {
    $scope.selected = true;
  }

  self.submit = function () {
    self.successMessage = "";
    self.failureMessage = "";
    var errorArr = [];

    self.visit.time.value = self.visit.time.value.toLowerCase();

    var fieldsToSend = ["date", "time", "notes", "patient", "type", "hospital", "icdCode", "height", "weight", "houseSmokingStatus"];
    if (self.visit.patient.age < 0)
      fieldsToSend = fieldsToSend.concat(["headCircumference", "patientSmokingStatus", "systolic", "diastolic", "hdl", "ldl", "tri"]);
    else if (self.visit.patient.age < 3)
      fieldsToSend = fieldsToSend.concat(["headCircumference"]);
    else if (self.visit.patient.age < 12)
      fieldsToSend = fieldsToSend.concat(["systolic", "diastolic"]);
    else
      fieldsToSend = fieldsToSend.concat(["patientSmokingStatus", "systolic", "diastolic", "hdl", "ldl", "tri"]);

    var errorArr = [];
    for (var i = 0; i < fieldsToSend.length; i++) {
      var field = fieldsToSend[i];
      if (self.visit[field].invalid) {
        errorArr.push(self.visit[field].name + " field is invalid.");
      }
    }

    if (!self.prescription.ndc.isNone) {
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
    }

    if (errorArr.length > 0) {
      $scope.message = "";
      $scope.errorMsg = "Error: " + errorArr.join(" ");
      return;
    }

    //all error checking done
    var infoToSend = { hcp: $('meta[name="remoteUser"]').attr('content'), status: "PENDING" };
    for (var i = 0; i < fieldsToSend.length; i++) {
      var field = fieldsToSend[i];
      infoToSend[field] = self.visit[field].value + "";
    }

    delete infoToSend.prescheduled;
    infoToSend.actualPatient = self.visit.patient.actualPatient;
    // if (self.visit.prescheduled.value === true)
    //   infoToSend.preScheduled = "true";

    var httpRequest = {
      method: 'POST',
      url: '/iTrust2/api/v1/officevisits',
      data: infoToSend,
    };

    console.log(JSON.stringify(infoToSend));

    $http(httpRequest).then(function (response) {
      if (self.prescription.ndc.isNone) {
        $scope.message = "Office visit created successfully";
        $scope.errorMsg = "";
      } else {
        submitPrescription(response.data);
      }
    }, function (rejection) {
      $scope.message = "";
      $scope.errorMsg = "Error occurred creating office visit";
      console.log(rejection);
    })
  }

  function submitPrescription(officeResponse) {
    var infoToSend = {};
    infoToSend.patient = self.visit.patient.actualPatient.self.username;
    infoToSend.officeVisitId = officeResponse.id;
    infoToSend.ndc = self.prescription.ndc.value;
    infoToSend.startDate = self.prescription.startDate.value
    infoToSend.endDate = self.prescription.endDate.value
    infoToSend.dosage = self.prescription.dosage.value;
    infoToSend.numRenewals = self.prescription.renewals.value;

    console.log(infoToSend);

    var httpRequest = {
      method: 'POST',
      url: '/iTrust2/api/v1/prescriptions',
      data: infoToSend,
    };

    $http(httpRequest).then(function (response) {
      $scope.message = "Office visit created successfully";
      $scope.errorMsg = "";
      console.log(response);
    }, function (rejection) {
      $scope.message = "";
      $scope.errorMsg = "Error occurred creating prescription";
      console.log(rejection);
    })
  }

});