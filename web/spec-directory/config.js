// const data =
// [
// "https://raw.githubusercontent.com/egovernments/egov-services/master/docs/swm/contract/v1-0-0.yaml"
// // "https://raw.githubusercontent.com/egovernments/egov-services/master/docs/asset/contracts/v1-0-0.yml"
// ]

const data = {
  swm: {
    isSpecificHeader: false,
    url:
      'https://raw.githubusercontent.com/egovernments/egov-services/master/docs/swm/contract/v1-0-0.yaml',
    masters: []
  },

  asset: {
    isSpecificHeader: false,
    url:
      'https://raw.githubusercontent.com/egovernments/egov-services/master/docs/asset/contracts/v1-0-0.yml',
    masters: []
  },

  lcms: {
    isSpecificHeader: true,
    url:
      'https://raw.githubusercontent.com/egovernments/egov-services/master/docs/lcms/contracts/lcms-services/v1-0-0.yml',
    masters: [
      {
        masterName: 'court',
        moduleDefinition:
          'https://raw.githubusercontent.com/egovernments/egov-services/master/docs/lcms/contracts/lcms-services/v1-0-0.yml#/definitions/Court',
        uniqueKeys: ['code', 'tenantId'],
        specificHeaders: [
          'name',
          'code',
          'active',
          'type',
          'tenantId',
          'addressLine1',
          'addressLine2',
          'city',
          'pincode'
        ]
      }
    ]
  },

  pm: {
    isSpecificHeader: false,
    url:
      'https://raw.githubusercontent.com/egovernments/egov-services/master/docs/performance-assessment/contracts/v1-1-0.yml',
    masters: []
  },

  inventory: {
    isSpecificHeader: false,
    url:
      'https://raw.githubusercontent.com/egovernments/egov-services/master/docs/inventory/contracts/V1-0-0.yaml',
    masters: []
  },
  works: {
    isSpecificHeader: false,
    url:
      'https://raw.githubusercontent.com/egovernments/egov-services/master/docs/works/contracts/works-common/v1-0-0.yml',
    masters: []
  },
  pt: {
    isSpecificHeader: false,
    url:
      'https://raw.githubusercontent.com/kaviyarasan1993/kavi_egov/master/pt-api-spec.yaml',
    masters: []
  }
};

// exports.data = data;
exports.data = data;
