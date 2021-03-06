var dat = {
	"wc.create": {
		"numCols": 12/3,
		"url": "/wcms/masters/treatmentplants/_create",
		"tenantIdRequired": true,
		"idJsonPath": "TreatmentPlants[0].code",
		"useTimestamp": true,
		"objectName": "TreatmentPlants",
		"groups": [
			{
				"label": "wc.create.groups.treatmentplant.title",
				"name": "createTreatmentPlant",
				"fields": [
						{
							"name": "name",
							"jsonPath": "TreatmentPlants[0].name",
							"label": "wc.create.groups.fields.treatmentPlantName",
							"pattern": "^.{3,100}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of name is 100"
						},
						{
							"name": "plantType",
							"jsonPath": "TreatmentPlants[0].plantType",
							"label": "wc.create.groups.fields.plantType",
							"pattern": "",
							"type": "singleValueList",
							"url": "/wcms/masters/master/_getplanttypes?|$..key|$..object",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": ""
						},
						{
							"name": "locationName",
							"jsonPath": "TreatmentPlants[0].location",
							"label": "wc.create.groups.fields.location",
							"pattern": "^.{0,100}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of Location is 100"
						},
						{
							"name": "storageReservoirName",
							"jsonPath": "TreatmentPlants[0].storageReservoirName",
							"label": "wc.create.groups.fields.storageType",
							"pattern": "",
							"type": "singleValueList",
							"url": "/wcms/masters/storagereservoirs/_search?|$..name|$..name",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": ""
						},
						{
							"name": "capacity",
							"jsonPath": "TreatmentPlants[0].capacity",
							"label": "wc.create.groups.fields.storageCapacityofReservoir(in MLD)",
							"pattern": "^.{1,8}$",
							"type": "number",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of capacity is 8"
						},
						{
							"name": "description",
							"jsonPath": "TreatmentPlants[0].description",
							"label": "wc.create.groups.fields.description",
							"pattern": "^.{0,250}$",
							"type": "text",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of description is 250"
						}
				]
			}
		]
	},
	"wc.search": {
		"numCols": 12/3,
		"url": "/wcms/masters/treatmentplants/_search",
		"tenantIdRequired": true,
		"useTimestamp": true,
		"objectName": "treatmentplant",
		"groups": [
			{
				"label": "wc.search.groups.treatmentplant.title",
				"name": "searchTreatmentPlant",
				"fields": [
					{
						"name": "name",
						"jsonPath": "name",
						"label": "wc.create.groups.fields.treatmentPlantName",
						"pattern": "^.{3,100}$",
						"type": "text",
						"isRequired": false,
						"isDisabled": false,
						"requiredErrMsg": "",
						"patternErrMsg": "Maximum length of name is 100"
					},
					{
						"name": "plantType",
						"jsonPath": "plantType",
						"label": "wc.create.groups.fields.plantType",
						"pattern": "",
						"type": "singleValueList",
						"url": "/wcms/masters/master/_getplanttypes?|$..key|$..object",
						"isRequired": false,
						"isDisabled": false,
						"requiredErrMsg": "",
						"patternErrMsg": ""
					}
				]
			}
		],
		"result": {
			"header": [{label: "wc.create.groups.fields.treatmentPlantName"},{label: "wc.create.groups.fields.plantType"},{label: "wc.create.groups.fields.location"},
			{label: "wc.create.groups.fields.storageCapacityofReservoir(in MLD)"},{label: "wc.create.groups.fields.storageReservoirName"}],
			"values": ["name", "plantType","location","capacity","storageReservoirName"],
			"resultPath": "TreatmentPlants",
			"rowClickUrlUpdate": "/update/wc/treatmentPlants/{id}",
			"rowClickUrlView": "/view/wc/treatmentPlants/{id}"
			}
	},
	"wc.view": {
		"numCols": 12/3,
		"url": "/wcms/masters/treatmentplants/_search?ids={id}",
		"tenantIdRequired": true,
		"useTimestamp": true,
		"objectName": "TreatmentPlants",
		"groups": [
			{
				"label": "wc.view.groups.treatmentplant.title",
				"name": "viewTreatmentPlant",
				"fields": [
						{
							"name": "name",
							"jsonPath": "TreatmentPlants[0].name",
							"label": "wc.create.groups.fields.treatmentPlantName",
							"pattern": "^.{3,100}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of name is 100"
						},
						{
							"name": "plantType",
							"jsonPath": "TreatmentPlants[0].plantType",
							"label": "wc.create.groups.fields.plantType",
							"pattern": "",
							"type": "singleValueList",
							"url": "/wcms/masters/master/_getplanttypes?|$..key|$..object",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": ""
						},
						{
							"name": "locationName",
							"jsonPath": "TreatmentPlants[0].location",
							"label": "wc.create.groups.fields.location",
							"pattern": "^.{0,100}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of Location is 100"
						},
						{
							"name": "storageReservoirName",
							"jsonPath": "TreatmentPlants[0].storageReservoirName",
							"label": "wc.create.groups.fields.storageType",
							"pattern": "",
							"type": "singleValueList",
							"url": "/wcms/masters/storagereservoirs/_search?|$..name|$..name",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": ""
						},
						{
							"name": "capacity",
							"jsonPath": "TreatmentPlants[0].capacity",
							"label": "wc.create.groups.fields.storageCapacityofReservoir(in MLD)",
							"pattern": "^.{1,8}$",
							"type": "number",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of capacity is 8"
						},
						{
							"name": "description",
							"jsonPath": "TreatmentPlants[0].description",
							"label": "wc.create.groups.fields.description",
							"pattern": "^.{3,250}$",
							"type": "text",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of description is 250"
						}
				]
			}
		]
	},
	"wc.update": {
		"numCols": 12/3,
		"searchUrl": "/wcms/masters/treatmentplants/_search?ids={id}",
		"url":"/wcms/masters/treatmentplants/_update",
		"tenantIdRequired": true,
		"useTimestamp": true,
		"objectName": "TreatmentPlants",
		"groups": [
			{
				"label": "wc.update.groups.treatmentplant.title",
				"name": "updateTreatmentPlant",
				"fields": [
						{
							"name": "name",
							"jsonPath": "TreatmentPlants[0].name",
							"label": "wc.create.groups.fields.treatmentPlantName",
							"pattern": "^.{3,100}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of name is 100"
						},
						{
							"name": "plantType",
							"jsonPath": "TreatmentPlants[0].plantType",
							"label": "wc.create.groups.fields.plantType",
							"pattern": "",
							"type": "singleValueList",
							"url": "/wcms/masters/master/_getplanttypes?|$..key|$..object",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": ""
						},
						{
							"name": "locationName",
							"jsonPath": "TreatmentPlants[0].location",
							"label": "wc.create.groups.fields.location",
							"pattern": "^.{0,100}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of Location is 100"
						},
						{
							"name": "storageReservoirName",
							"jsonPath": "TreatmentPlants[0].storageReservoirName",
							"label": "wc.create.groups.fields.storageType",
							"pattern": "",
							"type": "singleValueList",
							"url": "/wcms/masters/storagereservoirs/_search?|$..name|$..name",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": ""
						},
						{
							"name": "capacity",
							"jsonPath": "TreatmentPlants[0].capacity",
							"label": "wc.create.groups.fields.storageCapacityofReservoir(in MLD)",
							"pattern": "^.{1,8}$",
							"type": "number",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of capacity is 8"
						},
						{
							"name": "description",
							"jsonPath": "TreatmentPlants[0].description",
							"label": "wc.create.groups.fields.description",
							"pattern": "^.{0,250}$",
							"type": "text",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Maximum length of description is 250"
						}
				]
			}
		]
	}
}

export default dat;
