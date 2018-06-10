'use strict';
function makeVisitObject() {
   
	var newVisit = {};
    
    newVisit.date = {
        value: "",
        name: "Date",
        d: -1,
        m: -1,
        y: -1,
        invalid: true,
        errorMessage: "",
        verify: function () {
        	    
            var daysInMonths = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
            var monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
            var errorArr = [];
            if (/^\d{2}\/\d{2}\/\d{4}$/.test(this.value)) {
                var split = this.value.split("/");
                var mm = +split[0], dd = +split[1], yyyy = +split[2];
                this.m = mm;
                this.d = dd;
                this.y = yyyy;
                if (mm == 0 || mm > 12) {
                    errorArr.push(split[0] + " is not a valid month.");
                } else if (dd == 0) {
                    errorArr.push("00 is not a valid day.");
                } else if (dd > daysInMonths[mm - 1]) {
                    if (mm == 2 && dd == 29) {
                        // quick calculation of leap year, which happens on years divisible by 4,
                        // unless it's divisible by 100, unless it's divisible by 400.
                        var isLeapYear = (yyyy % 4 == 0 ? 1 : 0)
                            + (yyyy % 100 == 0 ? 1 : 0)
                            + (yyyy % 400 == 0 ? 1 : 0);
                        isLeapYear = (isLeapYear + 1) % 2;
                        if (isLeapYear)
                            errorArr.push(yyyy + " was not a leap year, so 02/29 is invalid.");
                    } else {
                        errorArr.push(mm + "/" + dd + " is not a valid day; there are only " + daysInMonths[mm - 1] + " days in " + monthNames[mm - 1] + ".");
                    }
                }
            }
            else {
                errorArr.push("Date must be in format MM/DD/YYYY.");
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
            calculateAge();
        }
    }
    newVisit.patient = {
        value: "",
        name: "Patient",
        invalid: true,
        actualPatient: {},
        patientList: [],
        age: -1,
        ageErrMess: "",
        verify: function () {
            this.invalid = (this.value == undefined || this.value.length == 0);
            for (var i = 0; i < this.patientList.length; i++) {
                if (this.patientList[i].self.username == this.value) {
                    this.actualPatient = this.patientList[i];
                    break;
                }
            }
            calculateAge();
        }
    }
    newVisit.prescheduled = {
        value: false
    }
    newVisit.type = {
        value: "",
        name: "Type",
        invalid: true,
        typeList: [],
        verify: function () {
            this.invalid = (this.value == undefined || this.value.length == 0);
        }
    }
    newVisit.hospital = {
        value: "",
        name: "Hospital",
        invalid: true,
        hospitalList: [],
        verify: function () {
            this.invalid = (this.value == undefined || this.value.length == 0);
        }
    }
    newVisit.time = {
        value: "",
        name: "Time",
        invalid: true,
        errorMessage: "",
        verify: function () {
            var errorArr = [];
            if (/^\d{1,2}:\d{2} (([ap]m)|([AP]M))$/.test(this.value)) {
                var split = this.value.split(/[: ]/);
                var hh = +split[0];
                var mm = +split[1];
                if (hh == 0 || hh > 12) {
                    errorArr.push(hh + " is not a valid hour.");
                }
                if (mm > 59) {
                    errorArr.push(mm + " is not a valid number of minutes.");
                }
            } else {
                errorArr.push("Example of proper format: 08:30 am");
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    newVisit.notes = {
        value: "",
        name: "Notes",
        invalid: false,
        errorMessage: "Notes must be 500 characters or less.",
        verify: function () {
            this.invalid = this.value.length > 500;
        }
    }
    //diagnosis:
    newVisit.icdCode = {
        value: "",
        label: "",
        selected: {},
        name: "Diagnosis",
        options: [],
        verify: function () {
            this.value = this.selected.code;
            console.log("value: " + this.value);
        }
    }
    //metrics:
    newVisit.height = {
        value: "",
        name: "Height",
        invalid: true,
        errorMessage: "",
        verify: function () {
            var errorArr = [];
            if (/^[-]?\d{1,3}([.]\d)?$/.test(this.value)) {
                var num = +this.value;
                if (num <= 0)
                    errorArr.push("Height must be greater than 0.");
            } else {
                errorArr.push("Height must be 1-3 digits, plus at most 1 decimal place.")
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    newVisit.weight = {
        value: "",
        name: "Weight",
        invalid: true,
        errorMessage: "",
        verify: function () {
            var errorArr = [];
            if (/^[-]?\d{1,4}([.]\d)?$/.test(this.value)) {
                var num = +this.value;
                if (num <= 0)
                    errorArr.push("Weight must be greater than 0.");
            } else {
                errorArr.push("Weight must be 1-4 digits, plus at most 1 decimal place.")
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    newVisit.headCircumference = {
        value: "",
        name: "Head circumference",
        invalid: true,
        errorMessage: "",
        verify: function () {
            var errorArr = [];
            if (/^[-]?\d{1,3}([.]\d)?$/.test(this.value)) {
                var num = +this.value;
                if (num <= 0)
                    errorArr.push("Head circumference must be greater than 0.");
            } else {
                errorArr.push("Head circumference must be 1-3 digits, plus at most 1 decimal place.")
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    newVisit.systolic = {
        value: "",
        name: "Systolic blood pressure",
        invalid: true,
        errorMessage: "",
        verify: function () {
            var errorArr = [];
            if (/^[-]?\d{1,3}$/.test(this.value)) {
                var num = +this.value;
                if (num <= 0)
                    errorArr.push("Systolic pressure must be greater than 0.");
            } else {
                errorArr.push("Systolic pressure must be 1-3 digits.")
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    newVisit.diastolic = {
        value: "",
        name: "Diastolic blood pressure",
        invalid: true,
        errorMessage: "",
        verify: function () {
            var errorArr = [];
            if (/^[-]?\d{1,3}$/.test(this.value)) {
                var num = +this.value;
                if (num <= 0)
                    errorArr.push("Diastolic pressure must be greater than 0.");
            } else {
                errorArr.push("Diastolic pressure must be 1-3 digits.")
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    newVisit.hdl = {
        value: "",
        name: "HDL",
        invalid: true,
        errorMessage: "",
        verify: function () { cholestCheck.call(this, 0, 90, "Invalid field. HDL must be between 0-90 [inclusive]."); }
    }
    newVisit.ldl = {
        value: "",
        name: "LDL",
        invalid: true,
        errorMessage: "",
        verify: function () { cholestCheck.call(this, 0, 600, "Invalid field. HDL must be between 0-600 [inclusive]."); }
    }
    newVisit.tri = {
        value: "",
        name: "Triglycerides",
        invalid: true,
        errorMessage: "",
        verify: function () { cholestCheck.call(this, 100, 600, "Invalid field. Triglycerides must be between 100-600 [inclusive]."); }
    }
    function cholestCheck(lower, upper, errMess) {
        this.invalid = false;
        this.errorMessage = errMess;
        if (/^[-]?\d+$/.test(this.value)) {
            var num = +this.value;
            if (num < lower || num > upper)
                this.invalid = true;
        } else {
            this.invalid = true;
        }
    }
    newVisit.houseSmokingStatus = {
        value: "",
        name: "Household smoking status",
        invalid: true,
        options: [],
        verify: function () {
            this.invalid = (this.value == undefined || this.value.length == 0);
        }
    }
    newVisit.patientSmokingStatus = {
        value: "",
        name: "Patient smoking status",
        invalid: true,
        options: [],
        verify: function () {
            this.invalid = (this.value == undefined || this.value.length == 0);
        }
    }

    function calculateAge() {
        if (newVisit.date.invalid || newVisit.patient.invalid) {
            newVisit.patient.age = -1;
            return;
        }
        var p = newVisit.patient.actualPatient;
        if (!newVisit.date.invalid && p != undefined) {
            var pd = p.dateOfBirth.dayOfMonth, pm = p.dateOfBirth.month + 1, py = p.dateOfBirth.year;
            var od = newVisit.date.d, om = newVisit.date.m, oy = newVisit.date.y;
            var pbDate = new Date(py, pm, pd);
            var ovDate = new Date(oy, om, od);
            var elapsedYears = oy - py - 1;
            if (om > pm || om == pm && od >= pd)
                elapsedYears += 1;
            if (elapsedYears < 0) {
                newVisit.patient.ageErrMess = "Error: cannot schedule appointment with patient before they are born."
                newVisit.patient.age = -1;
            } else {
                newVisit.patient.age = elapsedYears;
            }
        } else {
            newVisit.patient.ageErrMess = "Please input office visit date and patient in order to add age-specific health metrics."
        }
    }

    return newVisit;
}

