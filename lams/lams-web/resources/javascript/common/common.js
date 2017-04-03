var baseUrl = window.location.origin;

var authToken=localStorage.getItem("auth-token");

//request info from cookies
var requestInfo = {
    "apiId":"org.egov.pgr",
    "ver":"1.0",
    "ts": "2015-04-12",
    "action":"asd",
    "did":"4354648646",
    "key":"xyz",
    "msgId":"654654",
    "requesterId":"61",
    "authToken":authToken
};

var tenantId="ap.kurnool";



// var employeeType=JSON.parse(localStorage.getItem("employeeType"))==null?(localStorage.setItem("employeeType",JSON.stringify(getCommonMaster("hr-masters", "employeetypes", "EmployeeType").responseJSON["EmployeeType"]))|| []) :JSON.parse(localStorage.getItem("employeeType"));
// var employeeStatus=JSON.parse(localStorage.getItem("employeeStatus"))==null?(localStorage.setItem("employeeStatus",JSON.stringify(getCommonMaster("hr-masters", "hrstatuses", "HRStatus").responseJSON["HRStatus"])) || []) :JSON.parse(localStorage.getItem("employeeStatus"));
// var group=JSON.parse(localStorage.getItem("group"))==null?(localStorage.setItem("group",JSON.stringify(getCommonMaster("hr-masters", "groups", "Group").responseJSON["Group"])) || []) :JSON.parse(localStorage.getItem("group"));
// var maritalStatus=JSON.parse(localStorage.getItem("maritalStatus"))==null?(localStorage.setItem("maritalStatus",JSON.stringify(["MARRIED", "UNMARRIED", "DIVORCED", "WIDOWER", "WIDOW"]))) :JSON.parse(localStorage.getItem("maritalStatus"));
// var user_bloodGroup=JSON.parse(localStorage.getItem("user_bloodGroup"))==null?(localStorage.setItem("user_bloodGroup",JSON.stringify(["O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"]))) :JSON.parse(localStorage.getItem("user_bloodGroup"));
// var motherTounge=JSON.parse(localStorage.getItem("motherTounge"))==null?(localStorage.setItem("motherTounge",JSON.stringify(getCommonMaster("egov-common-masters", "languages", "Language").responseJSON["Language"])) || []) :JSON.parse(localStorage.getItem("motherTounge"));
// var religion=JSON.parse(localStorage.getItem("religion"))==null?(localStorage.setItem("religion",JSON.stringify(getCommonMaster("egov-common-masters", "religions", "Religion").responseJSON["Religion"])) || []) :JSON.parse(localStorage.getItem("religion"));
// var community=JSON.parse(localStorage.getItem("community"))==null?(localStorage.setItem("community",JSON.stringify(getCommonMaster("egov-common-masters", "communities", "Community").responseJSON["Community"])) || []) :JSON.parse(localStorage.getItem("community"));
// var category=JSON.parse(localStorage.getItem("category"))==null?(localStorage.setItem("category",JSON.stringify(getCommonMaster("egov-common-masters", "categories", "Category").responseJSON["Category"])) || []) :JSON.parse(localStorage.getItem("category"));
// var bank=JSON.parse(localStorage.getItem("bank"))==null?(localStorage.setItem("bank",JSON.stringify(getCommonMaster("egf-masters", "banks", "banks").responseJSON["banks"])) || []) :JSON.parse(localStorage.getItem("bank"));
// var recruitmentMode=JSON.parse(localStorage.getItem("recruitmentMode"))==null?(localStorage.setItem("recruitmentMode",JSON.stringify(getCommonMaster("hr-masters", "recruitmentmodes", "RecruitmentMode").responseJSON["RecruitmentMode"])) || []) :JSON.parse(localStorage.getItem("recruitmentMode"));
// var recruitmentType=JSON.parse(localStorage.getItem("recruitmentType"))==null?(localStorage.setItem("recruitmentType",JSON.stringify(getCommonMaster("hr-masters", "recruitmenttypes", "RecruitmentType").responseJSON["RecruitmentType"])) || []) :JSON.parse(localStorage.getItem("recruitmentType"));
// var recruitmentQuota=JSON.parse(localStorage.getItem("recruitmentQuota"))==null?(localStorage.setItem("recruitmentQuota",JSON.stringify(getCommonMaster("hr-masters", "recruitmentquotas", "RecruitmentQuota").responseJSON["RecruitmentQuota"])) || []) :JSON.parse(localStorage.getItem("recruitmentQuota"));
// var assignments_grade=JSON.parse(localStorage.getItem("assignments_grade"))==null?(localStorage.setItem("assignments_grade",JSON.stringify(getCommonMaster("hr-masters", "grades", "Grade").responseJSON["Grade"])) || []) :JSON.parse(localStorage.getItem("assignments_grade"));
var designation = !localStorage.getItem("assignments_designation") || localStorage.getItem("assignments_designation") == "undefined" ? (localStorage.setItem("assignments_designation", JSON.stringify(getCommonMaster("hr-masters", "designations", "Designation").responseJSON["Designation"])) || []) : JSON.parse(localStorage.getItem("assignments_designation"));
var department = !localStorage.getItem("assignments_department") || localStorage.getItem("assignments_department") == "undefined" ? (localStorage.setItem("assignments_department", JSON.stringify(getCommonMaster("egov-common-masters", "departments", "Department").responseJSON["Department"])) || []) : JSON.parse(localStorage.getItem("assignments_department"));
var locality = !localStorage.getItem("locality") || localStorage.getItem("locality") == "undefined" ? (localStorage.setItem("locality", JSON.stringify(commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"LOCALITY",hierarchyTypeName:"LOCATION"}).responseJSON["Boundary"])) || []) : JSON.parse(localStorage.getItem("locality"));
var electionwards = !localStorage.getItem("ward") || localStorage.getItem("ward") == "undefined" ? (localStorage.setItem("ward", JSON.stringify(commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"WARD",hierarchyTypeName:"ADMINISTRATION"}).responseJSON["Boundary"])) || []) : JSON.parse(localStorage.getItem("ward"));
var street = !localStorage.getItem("street") || localStorage.getItem("street") == "undefined" ? (localStorage.setItem("street", JSON.stringify(commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"STREET",hierarchyTypeName:"LOCATION"}).responseJSON["Boundary"])) || []) : JSON.parse(localStorage.getItem("street"));
var revenueWards = !localStorage.getItem("revenueWard") || localStorage.getItem("revenueWard") == "undefined" ? (localStorage.setItem("revenueWard", JSON.stringify(commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"WARD",hierarchyTypeName:"REVENUE"}).responseJSON["Boundary"])) || []) : JSON.parse(localStorage.getItem("revenueWard"));
var revenueZone = !localStorage.getItem("revenueZone") || localStorage.getItem("revenueZone") == "undefined" ? (localStorage.setItem("revenueZone", JSON.stringify(commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"ZONE",hierarchyTypeName:"REVENUE"}).responseJSON["Boundary"])) || []) : JSON.parse(localStorage.getItem("revenueZone"));
var revenueBlock = !localStorage.getItem("revenueBlock") || localStorage.getItem("revenueBlock") == "undefined" ? (localStorage.setItem("revenueBlock", JSON.stringify(commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"BLOCK",hierarchyTypeName:"REVENUE"}).responseJSON["Boundary"])) || []) : JSON.parse(localStorage.getItem("revenueBlock"));
var assetCategories = !localStorage.getItem("assetCategories") || localStorage.getItem("assetCategories") == "undefined" ? (localStorage.setItem("assetCategories", JSON.stringify(commonApiPost("asset-services","assetCategories","_search",{}).responseJSON["AssetCategory"])) || []) : JSON.parse(localStorage.getItem("assetCategories"));
var natureOfAllotments = !localStorage.getItem("natureOfAllotments") || localStorage.getItem("natureOfAllotments") == "undefined" ? (localStorage.setItem("natureOfAllotments", JSON.stringify(commonApiPost("lams-services","","getnatureofallotment",{}).responseJSON)) || {}) : JSON.parse(localStorage.getItem("natureOfAllotments"));
var employees=[];
// var assignments_fund=JSON.parse(localStorage.getItem("assignments_fund"))==null?(localStorage.setItem("assignments_fund",JSON.stringify(getCommonMaster("egf-masters", "funds", "funds").responseJSON["funds"])) || []) :JSON.parse(localStorage.getItem("assignments_fund"));
// var assignments_functionary=JSON.parse(localStorage.getItem("assignments_functionary"))==null?(localStorage.setItem("assignments_functionary",JSON.stringify(getCommonMaster("egf-masters", "functionaries", "funds").responseJSON["functionaries"])) || []) :JSON.parse(localStorage.getItem("assignments_functionary"));
// var assignments_function=JSON.parse(localStorage.getItem("assignments_function"))==null?(localStorage.setItem("assignments_function",JSON.stringify(getCommonMaster("egf-masters", "functions", "functions").responseJSON["functions"])) || []) :JSON.parse(localStorage.getItem("assignments_function"));

// var response=$.ajax({
//           url: window.location.origin+"/user/_login?tenantId=ap.public&username=ramakrishna&password=demo&grant_type=password&scope=read",
//           type: 'POST',
//           dataType: 'json',
//           // data:JSON.stringify({RequestInfo: requestInfo}),
//           async: false,
//           contentType: 'application/json',
//           headers:{
//             'Authorization' :'Basic ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0'
//           }
//       });
//
//       if(response["statusText"]==="OK")
//       {
//           localStorage.setItem("auth-token", response.responseJSON["access_token"]);
//           authToken=response.responseJSON["access_token"];
//         // alert("Successfully added");
//       }
//       else {
//         alert(response["statusText"]);
//       }



function getCommonMaster(mainRoute,resource,returnObject) {
    return $.ajax({
              url: baseUrl+"/"+mainRoute+"/"+resource+"/_search?tenantId="+tenantId,
              type: 'POST',
              dataType: 'json',
              data:JSON.stringify({RequestInfo: requestInfo}),
              async: false,
              // crossDomain: true, // set this to ensure our $.ajaxPrefilter hook fires
              // processData: false, // We want this to remain an object for  $.ajaxPrefilter
              headers: {
                      'auth-token': authToken
              },
              contentType: 'application/json'
              // ,
              // success: function (result) {
              //     return result[returnObject];
              //     // console.log(result);
              //    // CallBack(result);
              // },
              // error: function (error) {
              //     return [];
              //     // console.log(error);
              // }
          });
    // return response.statusText==="Ok"?response.responseJSON[returnObject]:[];
}

function commonApiPost(context,resource="",action="",queryObject={}) {
  var url=baseUrl+"/"+context+(resource?"/"+resource:"")+(action?"/"+action:"")+(queryObject?"?":"");
  for (var variable in queryObject) {
      if (queryObject[variable]) {
        url+="&"+variable+"="+queryObject[variable];
      }
  }
  return $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            data:JSON.stringify({RequestInfo: requestInfo}),
            async: false,
            contentType: 'application/json',
            headers:{
              'auth-token' :authToken
            }
        });
}

function commonApiGet(context,resource="",action="",queryObject={}) {
  var url=baseUrl+"/"+context+(resource?"/"+resource:"")+(action?"/"+action:"")+(queryObject?"?":"");
  for (var variable in queryObject) {
      if (queryObject[variable]) {
        url+="&"+variable+"="+queryObject[variable];
      }
  }
  return $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
            headers: {
                    'auth-token': authToken
            },
            // data:JSON.stringify({RequestInfo: requestInfo}),
            async: false,
            contentType: 'application/json'
        });
}

function getUrlVars() {
    var vars = [],
        hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function getCommonMasterById(mainRoute,resource,returnObject,id) {
    return $.ajax({
              url: baseUrl+"/"+mainRoute+"/"+resource+"/_search?tenantId="+tenantId+"&"+"id="+id,
              type: 'POST',
              dataType: 'json',
              data:JSON.stringify({RequestInfo: requestInfo}),
              async: false,
              // crossDomain: true, // set this to ensure our $.ajaxPrefilter hook fires
              // processData: false, // We want this to remain an object for  $.ajaxPrefilter
              headers: {
                      'auth-token': authToken
              },
              contentType: 'application/json'
              // ,
              // success: function (result) {
              //     return result[returnObject];
              //     // console.log(result);
              //    // CallBack(result);
              // },
              // error: function (error) {
              //     return [];
              //     // console.log(error);
              // }
          });
    // return response.statusText==="Ok"?response.responseJSON[returnObject]:[];
}

function addMandatoryStart(validationObject, prefix = "") {
    for (var key in validationObject) {
        if (prefix === "") {
            if (validationObject[key].required) {
                $(`label[for=${key}]`).append(`<span> *</span>`);
            }
        } else {
            if (validationObject[key].required) {
                $(`label[for=${prefix}\\.${key}]`).append(`<span> *</span>`);
            }
        }

        // $(`#${key}`).attr("disabled",true);
    };
}


function getNameById(object,id,property="") {
    if (id==""||id==null) {
        return "";
    }
    for (var i = 0; i < object.length; i++) {
        if (property=="") {
            if (object[i].id==id) {
              return object[i].name;
            }
        }
        else {
          if (object[i].hasOwnProperty(property)) {
                if (object[i].id==id) {
                    return object[i][property];
                }
          }
          else {
              return "";
          }
        }
    }
    return "";
}

// commonApiPost("asset","assetCategories","",{boundaryTypeName:"LOCALITY",hierarchyTypeName:"LOCATION"})
