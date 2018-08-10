var CONST_API_GET_FILE =
  "/filestore/v1/files/id?tenantId=" + tenantId + "&fileStoreId=";

const makeAjaxUpload = function(file, docType, cb) {
  if (file.constructor == File) {
    let formData = new FormData();
    formData.append("jurisdictionId", tenantId);
    formData.append("module", "EIS");
    formData.append("file", file);
    $.ajax({
      url: baseUrl + "/filestore/v1/files?tenantId=" + tenantId,
      data: formData,
      cache: false,
      contentType: false,
      processData: false,
      type: "POST",
      success: function(res) {
        res.docType = docType;
        cb(null, res);
      },
      error: function(jqXHR, exception) {
        cb(jqXHR.responseText || jqXHR.statusText);
      }
    });
  } else {
    cb(null, {
      files: [
        {
          fileStoreId: file
        }
      ]
    });
  }
};

const uploadFiles = function(body, cb) {
  var files = body.LeaveApplication[0].docs;

  if (files.length) {
    console.log(files);

    var breakout = 0;
    var docs = [];
    let counter = files.length;
    for (let j = 0; j < files.length; j++) {
      if (files[j].file instanceof File) {
        makeAjaxUpload(files[j].file, files[j].docType, function(err, res) {
          if (breakout == 1) return;
          else if (err) {
            cb(err);
            breakout = 1;
          } else {
            counter--;
            docs.push(res.files[0].fileStoreId);
            if (counter == 0) {
              body.LeaveApplication[0].documents = body.LeaveApplication[0].documents.concat(
                docs
              );
              delete body.LeaveApplication[0].docs;
              cb(null, body);
            }
          }
        });
      } else {
        cb(new Error("Not a File"));
      }
    }
  } else {
    cb(null, body);
  }
};

function today() {
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth() + 1;
  var yyyy = today.getFullYear();
  if (dd < 10) {
    dd = "0" + dd;
  }
  if (mm < 10) {
    mm = "0" + mm;
  }
  var today = dd + "/" + mm + "/" + yyyy;

  return today;
}

class ApplyLeave extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [],
      leaveSet: {
        employee: "",
        name: "",
        code: "",
        leaveType: {
          id: ""
        },
        fromDate: "",
        toDate: "",
        availableDays: "",
        leaveGround: "",
        workingDays: "",
        reason: "",
        status: "",
        stateId: "",
        tenantId: tenantId,
        encashable: false,
        leaveDays: "",
        docs: [],
        documents: [],
        workflowDetails: {
          department: "",
          designation: "",
          assignee: "",
          action: "",
          status: ""
        }
      },
      leaveList: [],
      stateId: "",
      owner: "",
      leaveNumber: "",
      perfixSuffix: "",
      encloseHoliday: "",
      prefixHolidays: "",
      suffixHolidays: "",
      compensetorySet: {
        compensatoryForDate: ""
      },
      workedOnDays: []
    };
    this.handleChange = this.handleChange.bind(this);
    this.addOrUpdate = this.addOrUpdate.bind(this);
    this.handleChangeThreeLevel = this.handleChangeThreeLevel.bind(this);
    this.getPrimaryAssigmentDep = this.getPrimaryAssigmentDep.bind(this);
    this.isSecondSat = this.isSecondSat.bind(this);
    this.isFourthSat = this.isFourthSat.bind(this);
    this.calculate = this.calculate.bind(this);
  }

  componentDidMount() {
    if (window.opener && window.opener.document) {
      var logo_ele = window.opener.document.getElementsByClassName(
        "homepage_logo"
      );
      if (logo_ele && logo_ele[0]) {
        document.getElementsByClassName("homepage_logo")[0].src =
          logo_ele[0].getAttribute("src") &&
          logo_ele[0].getAttribute("src").indexOf("http") > -1
            ? logo_ele[0].getAttribute("src")
            : window.location.origin + logo_ele[0].getAttribute("src");
      }
    }

    $("#availableDays,#workingDays,#name,#code").prop("disabled", true);

    if (getUrlVars()["type"])
      $("#hp-citizen-title").text(
        titleCase(getUrlVars()["type"]) + " Leave Application"
      );
    var type = getUrlVars()["type"],
      _this = this;
    var id = getUrlVars()["id"];
    var asOnDate = _this.state.leaveSet.toDate,
      _leaveSet = {},
      employee;
    if (getUrlVars()["type"] === "view") {
      $("input,select,textarea").prop("disabled", true);
    }

    if (type === "view") {
      getCommonMasterById("hr-leave", "leaveapplications", id, function(
        err,
        res
      ) {
        if (res && res.LeaveApplication && res.LeaveApplication[0]) {
          _leaveSet = res && res.LeaveApplication && res.LeaveApplication[0];
          $("#fromDate").prop("value", _leaveSet.fromDate);
          $("#toDate").prop("value", _leaveSet.toDate);
          commonApiPost(
            "hr-employee",
            "employees",
            "_search",
            {
              tenantId,
              id: _leaveSet.employee
            },
            function(err, res1) {
              if (res1 && res1.Employee && res1.Employee[0]) {
                employee = res1 && res1.Employee && res1.Employee[0];
                _leaveSet.name = employee.name;
                _leaveSet.code = employee.code;
                _this.setState({
                  leaveSet: _leaveSet,
                  leaveNumber: _leaveSet.applicationNumber
                });
              } else {
                showError(
                  "Something went wrong. Please contact Administrator."
                );
              }
            }
          );
          if (!_leaveSet.leaveType) {
            var workedOnDate = _leaveSet.compensatoryForDate;
          }
          if (workedOnDate) {
            _this.setState({
              compensetorySet: {
                ..._this.state.compensetorySet,
                compensatoryForDate: workedOnDate
              },
              workedOnDays: [workedOnDate]
            });
          }
        } else {
          showError("Something went wrong. Please contact Administrator.");
        }
      });
    } else {
      var hrConfigurations = [],
        allHolidayList = [];
      commonApiPost(
        "hr-masters",
        "hrconfigurations",
        "_search",
        {
          tenantId
        },
        function(err, res) {
          if (res) {
            _this.setState({
              ..._this.state,
              hrConfigurations: res ? res : []
            });
          }
        }
      );

      commonApiPost(
        "egov-common-masters",
        "holidays",
        "_search",
        {
          tenantId,
          pageSize: 500
        },
        function(err, res) {
          // console.log("allHolidayList api response", res);
          _this.setState({
            allHolidayList: res ? res.Holiday : []
          });
        }
      );

      if (!id) {
        commonApiPost(
          "hr-employee",
          "employees",
          "_loggedinemployee",
          { tenantId },
          function(err, res) {
            if (res && res.Employee && res.Employee[0]) {
              var obj = res.Employee[0];
              _this.setState({
                leaveSet: {
                  ..._this.state.leaveSet,
                  name: obj.name,
                  code: obj.code,
                  employee: obj.id
                },
                departmentId: _this.getPrimaryAssigmentDep(obj, "department")
              });
            } else {
              showError("Something went wrong. Please contact Administrator.");
            }
          }
        );
      } else {
        getCommonMasterById("hr-employee", "employees", id, function(err, res) {
          if (res) {
            var obj = res.Employee[0];
            _this.setState({
              leaveSet: {
                ..._this.state.leaveSet,
                name: obj.name,
                code: obj.code,
                employee: obj.id
              },
              departmentId: _this.getPrimaryAssigmentDep(obj, "department")
            });
          }
        });
      }
    }
  }

  componentDidUpdate() {
    var type = getUrlVars()["type"],
      _this = this;
    var id = getUrlVars()["id"];

    $("#fromDate, #toDate").datepicker({
      format: "dd/mm/yyyy",
      autoclose: true
    });

    $("#fromDate, #toDate").on("change", function(e) {
      if (
        !(_this.state.leaveSet.leaveType && _this.state.leaveSet.leaveType.id)
      ) {
        showError(
          "Please select Leave Type before entering from date and to date."
        );
        $("#" + e.target.id).val("");
      }

      if (_this.state.leaveSet[e.target.id] != e.target.value) {
        var _from = $("#fromDate").val();
        var _to = $("#toDate").val();
        var _triggerId = e.target.id;

        _this.setState({
          leaveSet: {
            ..._this.state.leaveSet,
            [_triggerId]: $("#" + _triggerId).val()
          }
        });
        // console.log($("#fromDate"));
        // console.log(_from);
        // console.log(_to);

        if (_from && _to) {
          let dateParts1 = _from.split("/");
          let newDateStr =
            dateParts1[1] + "/" + dateParts1[0] + "/ " + dateParts1[2];
          let date1 = new Date(newDateStr);

          let dateParts2 = _to.split("/");
          let newDateStr1 =
            dateParts2[1] + "/" + dateParts2[0] + "/" + dateParts2[2];
          let date2 = new Date(newDateStr1);

          if (date1 > date2) {
            showError("From date must be before End date.");
            $("#" + _triggerId).val("");
          }

          _this.calculate();
        }
      }
    });
  }

  getPrimaryAssigmentDep(obj, type) {
    for (var i = 0; i < obj.assignments.length; i++) {
      if (obj.assignments[i].isPrimary) {
        return obj.assignments[i][type];
      }
    }
  }

  componentWillMount() {
    var _this = this;
    getCommonMaster("hr-leave", "leavetypes", function(err, res) {
      if (res) {
        _this.setState({
          leaveList: res.LeaveType
        });
      }
    });
  }

  calculate() {
    let _this = this;
    let asOnDate = _this.state.leaveSet.toDate;
    let fromDate = _this.state.leaveSet.fromDate;
    let workingDays = _this.state.leaveSet.workingDays;
    let leaveType = _this.state.leaveSet.leaveType.id;
    let employeeid = getUrlVars()["id"] || _this.state.leaveSet.employee;
    let allHolidayList = _this.state.allHolidayList;
    let hrConfigurations = _this.state.hrConfigurations;
    let enclosingDays = 0;
    let prefixSuffixDays = 0;

    //Calling enclosing Holiday api
    let enclosingHoliday = getNameById(
      _this.state.leaveList,
      _this.state.leaveSet.leaveType.id,
      "encloseHoliday"
    );
    //console.log("enclosingHoliday",_this.state.leaveSet);
    if (
      enclosingHoliday ||
      enclosingHoliday == "TRUE" ||
      enclosingHoliday == "true"
    ) {
      commonApiPost(
        "egov-common-masters",
        "holidays",
        "_search",
        { tenantId, fromDate, toDate: asOnDate, enclosedHoliday: true },
        function(err, res1) {
          if (res1) {
            enclosingDays = res1.Holiday.length;
            _this.setState({
              encloseHoliday: res1.Holiday
            });
          }

          //calling PrefixSuffix api
          let includePrefixSuffix = getNameById(
            _this.state.leaveList,
            _this.state.leaveSet.leaveType.id,
            "includePrefixSuffix"
          );
          if (
            includePrefixSuffix ||
            includePrefixSuffix == "TRUE" ||
            includePrefixSuffix == "true"
          ) {
            commonApiPost(
              "egov-common-masters",
              "holidays",
              "_searchprefixsuffix",
              { tenantId, fromDate, toDate: asOnDate },
              function(err, res2) {
                if (res2) {
                  prefixSuffixDays = res2.Holiday && res2.Holiday[0].noOfDays;
                  let prefixFromDate =
                    res2.Holiday && res2.Holiday[0].prefixFromDate;
                  let prefixToDate =
                    res2.Holiday && res2.Holiday[0].prefixToDate;
                  let suffixFromDate =
                    res2.Holiday && res2.Holiday[0].suffixFromDate;
                  let suffixToDate =
                    res2.Holiday && res2.Holiday[0].suffixToDate;
                  _this.setState({
                    perfixSuffix: res2.Holiday[0]
                  });
                  var holidayList = [],
                    m1 = fromDate.split("/")[1],
                    m2 = asOnDate.split("/")[1],
                    y1 = fromDate.split("/")[2],
                    y2 = asOnDate.split("/")[2];
                  for (var i = 0; i < allHolidayList.length; i++) {
                    if (
                      allHolidayList[i].applicableOn &&
                      +allHolidayList[i].applicableOn.split("/")[1] >= +m1 &&
                      +allHolidayList[i].applicableOn.split("/")[1] <= +m2 &&
                      +allHolidayList[i].applicableOn.split("/")[2] <= y1 &&
                      +allHolidayList[i].applicableOn.split("/")[2] >= y2
                    ) {
                      holidayList.push(
                        new Date(
                          allHolidayList[i].applicableOn.split("/")[2],
                          allHolidayList[i].applicableOn.split("/")[1] - 1,
                          allHolidayList[i].applicableOn.split("/")[0]
                        ).getTime()
                      );
                    }
                  }
                  //Calculate working days
                  var _days = 0;
                  var parts1 = $("#fromDate")
                    .val()
                    .split("/");
                  var parts2 = $("#toDate")
                    .val()
                    .split("/");
                  var startDate = new Date(
                    parts1[2],
                    +parts1[1] - 1,
                    parts1[0]
                  );
                  var endDate = new Date(parts2[2], +parts2[1] - 1, parts2[0]);
                  console.log(holidayList);
                  if (
                    hrConfigurations["HRConfiguration"]["Weekly_holidays"][0] ==
                    "5-day week"
                  ) {
                    for (
                      var d = startDate;
                      d <= endDate;
                      d.setDate(d.getDate() + 1)
                    ) {
                      if (
                        holidayList.indexOf(d.getTime()) == -1 &&
                        hrConfigurations["HRConfiguration"][
                          "Include_enclosed_holidays"
                        ][0] != "Y" &&
                        !(d.getDay() === 0 || d.getDay() === 6)
                      )
                        _days++;
                    }
                  } else if (
                    hrConfigurations["HRConfiguration"]["Weekly_holidays"][0] ==
                    "5-day week with 2nd Saturday holiday"
                  ) {
                    for (
                      var d = startDate;
                      d <= endDate;
                      d.setDate(d.getDate() + 1)
                    ) {
                      //console.log("date",_this.isSecondSat(d) + " " + _this.isFourthSat(d));
                      if (
                        holidayList.indexOf(d.getTime()) == -1 &&
                        hrConfigurations["HRConfiguration"][
                          "Include_enclosed_holidays"
                        ][0] != "Y" &&
                        !_this.isSecondSat(d) &&
                        d.getDay() != 0
                      ) {
                        _days++;
                      }
                    }
                  } else if (
                    hrConfigurations["HRConfiguration"]["Weekly_holidays"][0] ==
                    "5-day week with 2nd and 4th Saturday holiday"
                  ) {
                    for (
                      var d = startDate;
                      d <= endDate;
                      d.setDate(d.getDate() + 1)
                    ) {
                      if (
                        holidayList.indexOf(d.getTime()) == -1 &&
                        hrConfigurations["HRConfiguration"][
                          "Include_enclosed_holidays"
                        ][0] != "Y" &&
                        d.getDay() != 0 &&
                        !_this.isSecondSat(d) &&
                        !_this.isFourthSat(d)
                      )
                        _days++;
                    }
                  } else {
                    for (
                      var d = startDate;
                      d <= endDate;
                      d.setDate(d.getDate() + 1)
                    ) {
                      if (
                        holidayList.indexOf(d.getTime()) == -1 &&
                        hrConfigurations["HRConfiguration"][
                          "Include_enclosed_holidays"
                        ][0] != "Y" &&
                        !(d.getDay() === 0)
                      )
                        _days++;
                    }
                  }
                  console.log(_days, prefixSuffixDays, enclosingDays);
                  _this.setState({
                    leaveSet: {
                      ..._this.state.leaveSet,
                      workingDays: _days,
                      leaveDays: _days + prefixSuffixDays + enclosingDays
                    }
                  });
                  if (prefixSuffixDays) {
                    if (prefixFromDate && prefixToDate) {
                      commonApiPost(
                        "egov-common-masters",
                        "holidays",
                        "_search",
                        {
                          tenantId,
                          fromDate: prefixFromDate,
                          toDate: prefixToDate,
                          enclosedHoliday: true
                        },
                        function(err, res3) {
                          if (res3) {
                            _this.setState({
                              prefixHolidays: res3.Holiday
                            });
                          }
                        }
                      );
                    } else {
                      _this.setState({
                        prefixHolidays: ""
                      });
                    }
                    if (suffixFromDate && suffixToDate) {
                      commonApiPost(
                        "egov-common-masters",
                        "holidays",
                        "_search",
                        {
                          tenantId,
                          fromDate: suffixFromDate,
                          toDate: suffixToDate,
                          enclosedHoliday: true
                        },
                        function(err, res3) {
                          if (res3) {
                            _this.setState({
                              suffixHolidays: res3.Holiday
                            });
                          }
                        }
                      );
                    } else {
                      _this.setState({
                        suffixHolidays: ""
                      });
                    }
                  } else {
                    _this.setState({
                      prefixHolidays: ""
                    });

                    _this.setState({
                      suffixHolidays: ""
                    });
                  }
                }
              }
            );
          } else {
            _this.setState({
              perfixSuffix: ""
            });
          }
        }
      );
    } else {
      _this.setState({
        encloseHoliday: ""
      });
    }

    // setTimeout(function() {
    //   console.log(_days, prefixSuffixDays, enclosingDays);

    //   //console.log("calcluating:days+prefix+enclose ", _days + " " + prefixSuffixDays, " ", enclosingDays);
    //   _this.setState({
    //     leaveSet: {
    //       ..._this.state.leaveSet,
    //       workingDays: _days,
    //       leaveDays: _days + prefixSuffixDays + enclosingDays
    //     }
    //   });
    // }, 500);

    commonApiPost(
      "hr-leave",
      "eligibleleaves",
      "_search",
      {
        leaveType,
        tenantId,
        asOnDate,
        employeeid
      },
      function(err, res) {
        if (res) {
          var _day =
            res && res["EligibleLeave"] && res["EligibleLeave"][0]
              ? res["EligibleLeave"][0].noOfDays
              : "";
          if (_day <= 0 || _day == "") {
            _this.setState({
              leaveSet: {
                ..._this.state.leaveSet,
                availableDays: ""
              }
            });
            return showError("You do not have leave for this leave type.");
          } else {
            _this.setState({
              leaveSet: {
                ..._this.state.leaveSet,
                availableDays: _day
              }
            });
          }
        } else {
          return showError("You do not have leave for this leave type.");
        }
      }
    );
  }

  handleChangeThreeLevel(e, pName, name) {
    var _this = this,
      val = e.target.value;
    if (pName == "leaveType") {
      this.setState({
        leaveSet: {
          ...this.state.leaveSet,
          encashable: false,
          [pName]: {
            ...this.state.leaveSet[pName],
            [name]: e.target.value
          }
        }
      });

      if (_this.state.leaveSet.fromDate && _this.state.leaveSet.toDate)
        _this.calculate();
    } else {
      this.setState({
        leaveSet: {
          ...this.state.leaveSet,
          [pName]: {
            ...this.state.leaveSet[pName],
            [name]: e.target.value
          }
        }
      });
    }
  }

  handleChange(e, name) {
    if (name === "encashable") {
      let val = e.target.checked;
      if (e.target.checked && !(getUrlVars()["type"] === "view"))
        $("#leaveDays").prop("disabled", false);
      else $("#leaveDays").prop("disabled", true);
      let _this = this;
      var asOnDate = today();
      let leaveType =
        this.state.leaveSet.leaveType && this.state.leaveSet.leaveType.id;
      let employeeid = getUrlVars()["id"];

      commonApiPost(
        "hr-leave",
        "eligibleleaves",
        "_search",
        {
          leaveType,
          tenantId,
          asOnDate,
          employeeid
        },
        function(err, res) {
          if (res) {
            var _day =
              res && res["EligibleLeave"] && res["EligibleLeave"][0]
                ? res["EligibleLeave"][0].noOfDays
                : "";
            if (_day <= 0 || _day == "") {
              _this.setState({
                leaveSet: {
                  ..._this.state.leaveSet,
                  availableDays: "",
                  fromDate: "",
                  toDate: ""
                }
              });
              return showError("You do not have leave for this leave type.");
            } else {
              _this.setState({
                leaveSet: {
                  ..._this.state.leaveSet,
                  availableDays: _day,
                  encashable: val,
                  fromDate: "",
                  toDate: ""
                }
              });
            }
          } else {
            _this.setState({
              leaveSet: {
                ..._this.state.leaveSet,
                encashable: val,
                fromDate: "",
                toDate: ""
              }
            });
          }
        }
      );
    } else if (name === "documents") {
      var fileTypes = [
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/pdf",
        "image/png",
        "image/jpeg"
      ];

      if (e.currentTarget.files.length != 0) {
        for (var i = 0; i < e.currentTarget.files.length; i++) {
          //2097152 = 2mb
          if (
            e.currentTarget.files[i].size > 2097152 &&
            fileTypes.indexOf(e.currentTarget.files[i].type) == -1
          ) {
            $("#documents").val("");
            return showError(
              "Maximum file size allowed is 2 MB.\n Please upload only DOC, PDF, xls, xlsx, png, jpeg file."
            );
          } else if (e.currentTarget.files[i].size > 2097152) {
            $("#documents").val("");
            return showError("Maximum file size allowed is 2 MB.");
          } else if (fileTypes.indexOf(e.currentTarget.files[i].type) == -1) {
            $("#documents").val("");
            return showError(
              "Please upload only DOC, PDF, xls, xlsx, png, jpeg file."
            );
          }
        }

        let files = e.currentTarget.files;
        let docs = [];
        for (let i = 0; i < e.currentTarget.files.length; i++) {
          docs.push({ docType: name, file: e.currentTarget.files[i] });
        }

        this.setState({
          leaveSet: {
            ...this.state.leaveSet,
            docs: docs
          }
        });
      } else {
        this.setState({
          disciplinarySet: {
            ...this.state.disciplinarySet,
            docs: []
          }
        });
      }
    } else {
      this.setState({
        leaveSet: {
          ...this.state.leaveSet,
          [name]:
            e.target.type === "checkbox" ? e.target.checked : e.target.value
        }
      });
    }
    console.log(this.state.leaveSet);
  }

  close() {
    // widow.close();
    open(location, "_self").close();
  }

  isSecondSat(d) {
    return d.getDay() == 6 && Math.ceil(d.getDate() / 7) == 2;
  }

  isFourthSat(d) {
    return d.getDay() == 6 && Math.ceil(d.getDate() / 7) == 4;
  }

  addOrUpdate(e, mode) {
    e.preventDefault();
    var _this = this;

    if (
      _this.state.leaveSet.availableDays <= 0 &&
      _this.state.leaveSet.availableDays == ""
    ) {
      return showError("You do not have leave for this leave type.");
    }

    if (_this.state.leaveSet.leaveType) {
      let maxDays = getNameById(
        _this.state.leaveList,
        _this.state.leaveSet.leaveType.id,
        "maxDays"
      );
      if (maxDays && maxDays < _this.state.leaveSet.workingDays) {
        return showError(
          "Number of Leaves applied exceeds Maximum leaves permitted"
        );
      }
    }

    var employee;
    var asOnDate = today();
    var departmentId = this.state.departmentId;
    var leaveNumber = this.state.leaveNumber;
    var owner = this.state.owner;
    var tempInfo = Object.assign({}, this.state.leaveSet),
      type = getUrlVars()["type"];
    delete tempInfo.name;
    delete tempInfo.code;

    if (tempInfo.encashable && !tempInfo.leaveDays)
      return showError(
        "Total Leave Days cannot be empty or zero. Please enter Total Leave Days"
      );

    if (tempInfo.encashable && tempInfo.leaveDays && tempInfo.leaveDays < 1)
      return showError("Total Leave Days cannot be negative or zero.");

    if (
      tempInfo.encashable &&
      tempInfo.leaveDays &&
      tempInfo.leaveDays > tempInfo.availableDays
    )
      return showError(
        "Total Leave Days cannot be greater than available days"
      );

    if (tempInfo.encashable) {
      tempInfo.workingDays = tempInfo.leaveDays;
    }

    //console.log(this.state.perfixSuffix, this.state.encloseHoliday);

    let holidays = [];
    if (this.state.encloseHoliday) {
      for (let i = 0; i < this.state.encloseHoliday.length; i++)
        holidays.push(this.state.encloseHoliday[i].applicableOn);
    }

    tempInfo.prefixDate = this.state.perfixSuffix
      ? this.state.perfixSuffix.prefixFromDate
      : "";
    tempInfo.suffixDate = this.state.perfixSuffix
      ? this.state.perfixSuffix.suffixToDate
      : "";
    tempInfo.holidays = holidays;

    commonApiPost(
      "hr-employee",
      "hod/employees",
      "_search",
      { tenantId, asOnDate, departmentId, active: true },
      function(err, res) {
        if (res && res["Employee"] && res["Employee"][0]) {
          employee = res["Employee"][0];
        } else {
          return showError(
            "HOD does not exists for given date range Please assign the HOD."
          );
        }
        var assignments_designation = [];
        getDropdown("assignments_designation", function(res) {
          assignments_designation = res;
        });
        var designation;
        employee.assignments.forEach(function(item) {
          if (item.isPrimary) {
            designation = item.designation;
          }
        });
        var hodDesignation = getNameById(assignments_designation, designation);
        var hodDetails =
          employee.name + " - " + employee.code + " - " + hodDesignation;
        tempInfo.workflowDetails.assignee =
          employee.assignments && employee.assignments[0]
            ? employee.assignments[0].position
            : "";
        var body = {
            RequestInfo: requestInfo,
            LeaveApplication: [tempInfo]
          },
          _this = this;

        uploadFiles(body, function(err1, _body) {
          if (err1) {
            showError(err1);
          } else {
            $.ajax({
              url:
                baseUrl +
                "/hr-leave/leaveapplications/_create?tenantId=" +
                tenantId,
              type: "POST",
              dataType: "json",
              data: JSON.stringify(_body),
              contentType: "application/json",
              headers: {
                "auth-token": authToken
              },
              success: function(res) {
                var leaveNumber = res.LeaveApplication[0].applicationNumber;
                window.location.href = `app/hr/leavemaster/ack-page.html?type=Apply&applicationNumber=${leaveNumber}&owner=${hodDetails}`;
              },
              error: function(err) {
                if (
                  err.responseJSON &&
                  err.responseJSON.LeaveApplication &&
                  err.responseJSON.LeaveApplication[0] &&
                  err.responseJSON.LeaveApplication[0].errorMsg
                ) {
                  showError(err.responseJSON.LeaveApplication[0].errorMsg);
                } else {
                  showError(
                    "Something went wrong. Please contact Administrator."
                  );
                }
              }
            });
          }
        });
      }
    );
  }

  render() {
    let { handleChange, addOrUpdate, handleChangeThreeLevel } = this;
    let { leaveSet, perfixSuffix, workedOnDays } = this.state;
    let { compensatoryForDate } = this.state.compensetorySet;
    let {
      name,
      code,
      workingDays,
      availableDays,
      fromDate,
      toDate,
      leaveGround,
      reason,
      leaveType,
      leaveDays,
      encashable,
      documents
    } = leaveSet;
    let mode = getUrlVars()["type"];

    const renderOption = function(list) {
      if (list) {
        return list.map(item => {
          return (
            <option key={item.id} value={item.id}>
              {item.name}
            </option>
          );
        });
      }
    };
    const renderWorkedDate = function(list) {
      if (list) {
        return list.map((item, ind) => {
          return (
            <option
              key={ind}
              value={typeof item == "object" ? item.workedDate : item}
            >
              {typeof item == "object" ? item.workedDate : item}
            </option>
          );
        });
      }
    };
    const showActionButton = function() {
      if (mode === "create" || !mode) {
        return (
          <button type="submit" className="btn btn-submit">
            Apply
          </button>
        );
      }
    };

    const renderEnclosingHolidayTr = () => {
      let { encloseHoliday, prefixHolidays, suffixHolidays } = this.state;
      if (encloseHoliday || prefixHolidays || suffixHolidays) {
        let totalHolidayArr = [];
        prefixHolidays.length &&
          prefixHolidays.map(item => {
            totalHolidayArr.push(item);
          });
        encloseHoliday.length &&
          encloseHoliday.map(item => {
            totalHolidayArr.push(item);
          });
        suffixHolidays.length &&
          suffixHolidays.map(item => {
            totalHolidayArr.push(item);
          });

        if (totalHolidayArr.length) {
          return totalHolidayArr.map((item, ind) => {
            if (item.name != "Sunday" && item.name != "Second Saturday") {
              return (
                <tr key={ind}>
                  <td>{item.name}</td>
                  <td>{item.applicableOn}</td>
                </tr>
              );
            }
          });
        } else {
          return (
            <tr>
              <td colSpan="2" className="text-center">
                No Enclosing Holidays
              </td>
            </tr>
          );
        }
      }
    };

    const showEnclosingHolidayTable = () => {
      if (this.state.encloseHoliday) {
        return (
          <div>
            <div className="land-table">
              <table id="employeeTable" className="table table-bordered">
                <thead>
                  <tr>
                    <th>Holiday Name</th>
                    <th>Holiday Date </th>
                  </tr>
                </thead>
                <tbody>{renderEnclosingHolidayTr()}</tbody>
              </table>
            </div>
          </div>
        );
      }
    };
    const renderDocumentLinks = () => {
      return documents.map((doc, i) => {
        console.log(window.location.origin + CONST_API_GET_FILE + doc);
        return (
          <tr key={i}>
            <td>{i + 1}</td>
            <td>
              <a href={window.location.origin + CONST_API_GET_FILE + doc}>
                Download
              </a>
            </td>
          </tr>
        );
      });
    };
    const showAttachments = () => {
      return (
        <div>
          {documents &&
            documents.length > 0 && (
              <div className="land-table">
                <table id="employeeTable" className="table table-bordered">
                  <thead>
                    <tr>
                      <th>Sr.No</th>
                      <th>Documents</th>
                    </tr>
                  </thead>
                  <tbody>{renderDocumentLinks()}</tbody>
                </table>
              </div>
            )}
        </div>
      );
    };

    const showPrefix = () => {
      if (
        this.state.perfixSuffix &&
        this.state.perfixSuffix.prefixFromDate &&
        this.state.perfixSuffix.prefixToDate &&
        !this.state.leaveSet.encashable
      ) {
        return (
          <div className="row">
            <div className="col-sm-6">
              <div className="row">
                <div className="col-sm-6 label-text">
                  <label htmlFor="">Prefix From Date</label>
                </div>
                <div className="col-sm-6">
                  <input
                    type="text"
                    id="perfixFromDate"
                    name="perfixFromDate"
                    value={perfixSuffix.prefixFromDate}
                    disabled
                  />
                </div>
              </div>
            </div>
            <div className="col-sm-6">
              <div className="row">
                <div className="col-sm-6 label-text">
                  <label htmlFor="">Prefix To Date</label>
                </div>
                <div className="col-sm-6">
                  <input
                    type="text"
                    id="prefixToDate"
                    name="prefixToDate"
                    value={perfixSuffix.prefixToDate}
                    disabled
                  />
                </div>
              </div>
            </div>
          </div>
        );
      }
    };

    const showSuffix = () => {
      if (
        this.state.perfixSuffix &&
        this.state.perfixSuffix.suffixFromDate &&
        this.state.perfixSuffix.suffixToDate &&
        !this.state.leaveSet.encashable
      ) {
        return (
          <div className="row">
            <div className="col-sm-6">
              <div className="row">
                <div className="col-sm-6 label-text">
                  <label htmlFor="">Suffix From Date</label>
                </div>
                <div className="col-sm-6">
                  <input
                    type="text"
                    id="suffixFromDate"
                    name="suffixFromDate"
                    value={perfixSuffix.suffixFromDate}
                    disabled
                  />
                </div>
              </div>
            </div>
            <div className="col-sm-6">
              <div className="row">
                <div className="col-sm-6 label-text">
                  <label htmlFor="">Suffix To Date</label>
                </div>
                <div className="col-sm-6">
                  <input
                    type="text"
                    id="suffixToDate"
                    name="suffixToDate"
                    value={perfixSuffix.suffixToDate}
                    disabled
                  />
                </div>
              </div>
            </div>
          </div>
        );
      }
    };

    const showEncashable = () => {
      if (this.state.leaveSet.leaveType && this.state.leaveSet.leaveType.id) {
        let encashableLeaveType = getNameById(
          this.state.leaveList,
          this.state.leaveSet.leaveType.id,
          "encashable"
        );
        if (
          encashableLeaveType ||
          encashableLeaveType == "TRUE" ||
          encashableLeaveType == "true"
        ) {
          return (
            <div className="row">
              <div className="col-sm-6">
                <div className="row">
                  <div className="col-sm-6 label-text">
                    <label htmlFor="">En-cashable</label>
                  </div>
                  <div className="col-sm-6">
                    <input
                      type="checkbox"
                      id="encashable"
                      name="encashable"
                      checked={encashable === true ? true : false}
                      onChange={e => {
                        handleChange(e, "encashable", true);
                      }}
                      disabled={getUrlVars()["type"] === "view"}
                    />
                  </div>
                </div>
              </div>
            </div>
          );
        }
      }
    };

    const showDateRange = () => {
      if (!this.state.leaveSet.encashable) {
        return (
          <div>
            <div className="row">
              <div className="col-sm-6">
                <div className="row">
                  <div className="col-sm-6 label-text">
                    <label htmlFor="">
                      From Date <span>*</span>
                    </label>
                  </div>
                  <div className="col-sm-6">
                    <div className="text-no-ui">
                      <span>
                        <i className="glyphicon glyphicon-calendar" />
                      </span>
                      <input
                        type="text"
                        id="fromDate"
                        name="fromDate"
                        // value={fromDate}
                        // onChange={e => {
                        //   console.log("handleChange");
                        //   handleChange(e, "fromDate");
                        //   //this.calculate();
                        // }}
                        required
                      />
                    </div>
                  </div>
                </div>
              </div>
              <div className="col-sm-6">
                <div className="row">
                  <div className="col-sm-6 label-text">
                    <label htmlFor="">
                      To Date <span>*</span>{" "}
                    </label>
                  </div>
                  <div className="col-sm-6">
                    <div className="text-no-ui">
                      <span>
                        <i className="glyphicon glyphicon-calendar" />
                      </span>
                      <input
                        type="text"
                        id="toDate"
                        name="toDate"
                        // value={toDate}
                        // onChange={e => {
                        //   handleChange(e, "toDate");
                        //   // this.calculate();
                        // }}
                        required
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="row">
              <div className="col-sm-6">
                <div className="row">
                  <div className="col-sm-6 label-text">
                    <label htmlFor="">Working Days</label>
                  </div>
                  <div className="col-sm-6">
                    <input
                      type="number"
                      id="workingDays"
                      name="workingDays"
                      value={workingDays}
                      onChange={e => {
                        handleChange(e, "workingDays");
                      }}
                      disabled
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        );
      }
    };

    return (
      <div>
        <h3>
          {getUrlVars()["type"] ? titleCase(getUrlVars()["type"]) : "Apply"}{" "}
          Leave Application{" "}
        </h3>
        <form
          onSubmit={e => {
            addOrUpdate(e);
          }}
        >
          <fieldset>
            <div className="row">
              <div className="col-sm-6">
                <div className="row">
                  <div className="col-sm-6 label-text">
                    <label htmlFor="">Employee Name</label>
                  </div>
                  <div className="col-sm-6">
                    <input
                      type="text"
                      id="name"
                      name="name"
                      value={name}
                      onChange={e => {
                        handleChange(e, "name");
                      }}
                    />
                  </div>
                </div>
              </div>
              <div className="col-sm-6">
                <div className="row">
                  <div className="col-sm-6 label-text">
                    <label htmlFor="">Employee Code</label>
                  </div>
                  <div className="col-sm-6">
                    <input
                      type="text"
                      id="code"
                      name="code"
                      value={code}
                      onChange={e => {
                        handleChange(e, "code");
                      }}
                    />
                  </div>
                </div>
              </div>
            </div>

            <div className="row">
              {leaveType ? (
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label htmlFor="leaveType">
                        Leave Type<span>*</span>
                      </label>
                    </div>
                    <div className="col-sm-6">
                      <div className="styled-select">
                        <select
                          id="leaveType"
                          name="leaveType"
                          value={leaveType.id}
                          required="true"
                          onChange={e => {
                            handleChangeThreeLevel(e, "leaveType", "id");
                          }}
                        >
                          <option value=""> Select Leave Type</option>
                          {renderOption(this.state.leaveList)}
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label for="">
                        Worked On <span>*</span>
                      </label>
                    </div>
                    <div className="col-sm-6">
                      <div className="styled-select">
                        <select
                          name="compensatoryForDate"
                          id="compensatoryForDate"
                          value={compensatoryForDate}
                          onChange={e => {
                            handleChange(e, "compensatoryForDate");
                          }}
                          disabled
                          required
                        >
                          <option value="">Select worked on date</option>
                          {renderWorkedDate(workedOnDays)}
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              )}
              {!leaveType ? (
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label for="">
                        Compensetory Leave On <span>*</span>
                      </label>
                    </div>
                    <div className="col-sm-6">
                      <div className="text-no-ui">
                        <span>
                          <i className="glyphicon glyphicon-calendar" />
                        </span>
                        <input
                          type="text"
                          id="fromDate"
                          name="fromDate"
                          value={fromDate}
                          onChange={e => {
                            handleChange(e, "fromDate");
                          }}
                          required
                          disabled
                        />
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label htmlFor="Reason">
                        Reason <span>*</span>
                      </label>
                    </div>
                    <div className="col-sm-6">
                      <textarea
                        rows="4"
                        cols="50"
                        id="reason"
                        name="reason"
                        value={reason}
                        onChange={e => {
                          handleChange(e, "reason");
                        }}
                        required
                      />
                    </div>
                  </div>
                </div>
              )}
            </div>

            {leaveType && showEncashable()}
            {leaveType && showDateRange()}

            {leaveType && (
              <div className="row">
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label htmlFor="">Available Leave</label>
                    </div>
                    <div className="col-sm-6">
                      <input
                        type="number"
                        id="availableDays"
                        name="availableDays"
                        value={availableDays}
                        onChange={e => {
                          handleChange(e, "availableDays");
                        }}
                      />
                    </div>
                  </div>
                </div>
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label htmlFor="documents">Attachments</label>
                    </div>
                    <div className="col-sm-6 label-view-text">
                      <input
                        type="file"
                        name="documents"
                        id="documents"
                        onChange={e => {
                          handleChange(e, "documents");
                        }}
                        multiple
                      />
                    </div>
                  </div>
                </div>
              </div>
            )}

            {leaveType && showPrefix()}
            {leaveType && showSuffix()}

            {leaveType && (
              <div className="row">
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label htmlFor="">Total Leave Days</label>
                    </div>
                    <div className="col-sm-6">
                      <input
                        type="number"
                        id="leaveDays"
                        name="leaveDays"
                        value={leaveDays}
                        onChange={e => {
                          handleChange(e, "leaveDays");
                        }}
                        disabled
                      />
                    </div>
                  </div>
                </div>
                <div className="col-sm-6">
                  <div className="row">
                    <div className="col-sm-6 label-text">
                      <label htmlFor="">
                        Leave Grounds <span>*</span>
                      </label>
                    </div>
                    <div className="col-sm-6">
                      <input
                        type="text"
                        id="leaveGround"
                        name="leaveGround"
                        value={leaveGround}
                        pattern="[a-zA-Z][a-zA-Z ]+"
                        maxLength="50"
                        onChange={e => {
                          handleChange(e, "leaveGround");
                        }}
                        required
                      />
                    </div>
                  </div>
                </div>
              </div>
            )}

            {leaveType && showEnclosingHolidayTable()}
            {leaveType && showAttachments()}

            <div className="text-center">
              {showActionButton()} &nbsp;&nbsp;
              <button
                type="button"
                className="btn btn-close"
                onClick={e => {
                  this.close();
                }}
              >
                Close
              </button>
            </div>
          </fieldset>
        </form>
      </div>
    );
  }
}

ReactDOM.render(<ApplyLeave />, document.getElementById("root"));
