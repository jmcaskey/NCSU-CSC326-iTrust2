'use strict';
function makePrescriptionObject() {
    var presc = {};
    presc.patient = {
        value: "",
        invalid: true,
        name: "Patient",
        selected: {},
        options: [],
        verify: function () {
            this.invalid = (this.value == undefined || this.value.length == 0);
        }
    }
    presc.dateRange = {
        invalid: false,
        errorMessage: "Error: End date must be after start date."
    }
    presc.ndc = {
        value: "",
        selected: {},
        isNone: true,
        options: [],
        name: "NDC code",
        verify: function () {
            this.value = this.selected.code;
            this.isNone = this.selected.label == "<none>";
        }
    }
    presc.startDate = {
        value: "",
        name: "Prescription start date",
        invalid: true,
        errorMessage: "",
        d: -1,
        m: -1,
        y: -1,
        verify: function () {
            valiDate.call(this);
            valiDateBoth(presc);
        }
    }
    presc.endDate = {
        value: "",
        name: "Prescription end date",
        invalid: true,
        errorMessage: "",
        d: -1,
        m: -1,
        y: -1,
        verify: function () {
            valiDate.call(this);
            valiDateBoth(presc);
        }
    }
    presc.dosage = {
        value: "",
        invalid: true,
        name: "Prescription dosage",
        verify: function () {
            var errorArr = [];
            if (/^[-]?\d{1,4}$/.test(this.value)) {
                var num = +this.value;
                if (num <= 0)
                    errorArr.push("Dosage must be greater than 0.");
            } else {
                errorArr.push("Dosage must be 1-4 digits.")
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    presc.renewals = {
        value: "",
        invalid: true,
        name: "Number of prescription renewals",
        verify: function () {
            var errorArr = [];
            if (/^[-]?\d{1,2}$/.test(this.value)) {
                var num = +this.value;
                if (num <= 0)
                    errorArr.push("Renewals must be greater than 0.");
            } else {
                errorArr.push("Renewals must be 1-2 digits.")
            }
            this.invalid = errorArr.length > 0;
            this.errorMessage = "Invalid field. " + errorArr.join(" ");
        }
    }
    return presc;
}

//Validates a date. Get it?
function valiDate() {
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
}

function valiDateBoth(p) {
    if (p.startDate.invalid || p.endDate.invalid) {
        p.dateRange.invalid = false;
        return;
    }
    if ((new Date(p.startDate.value)).getTime() > (new Date(p.endDate.value)).getTime()) {
        p.dateRange.invalid = true;
    } else {
        p.dateRange.invalid = false;
    }
}