package org.egov.swm.persistence.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swm.domain.model.Pagination;
import org.egov.swm.domain.model.Shift;
import org.egov.swm.domain.model.ShiftSearch;
import org.egov.swm.domain.model.ShiftType;
import org.egov.swm.domain.service.DepartmentService;
import org.egov.swm.domain.service.ShiftTypeService;
import org.egov.swm.persistence.entity.ShiftEntity;
import org.egov.swm.web.contract.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class ShiftJdbcRepository extends JdbcRepository {

    public static final String TABLE_NAME = "egswm_shift";

    @Autowired
    private ShiftTypeService shiftTypeService;

    @Autowired
    private DepartmentService departmentService;

    public Boolean uniqueCheck(final String tenantId, final String fieldName, final String fieldValue,
            final String uniqueFieldName,
            final String uniqueFieldValue) {

        return uniqueCheck(TABLE_NAME, tenantId, fieldName, fieldValue, uniqueFieldName, uniqueFieldValue);
    }

    public Pagination<Shift> search(final ShiftSearch searchRequest) {

        String searchQuery = "select * from " + TABLE_NAME + " :condition  :orderby ";

        final Map<String, Object> paramValues = new HashMap<>();
        final StringBuffer params = new StringBuffer();

        if (searchRequest.getSortBy() != null && !searchRequest.getSortBy().isEmpty()) {
            validateSortByOrder(searchRequest.getSortBy());
            validateEntityFieldName(searchRequest.getSortBy(), ShiftSearch.class);
        }

        String orderBy = "order by code";
        if (searchRequest.getSortBy() != null && !searchRequest.getSortBy().isEmpty())
            orderBy = "order by " + searchRequest.getSortBy();

        if (searchRequest.getCodes() != null) {
            addAnd(params);
            params.append("code in (:codes)");
            paramValues.put("codes", new ArrayList<>(Arrays.asList(searchRequest.getCodes().split(","))));
        }

        if (searchRequest.getTenantId() != null) {
            addAnd(params);
            params.append("tenantId =:tenantId");
            paramValues.put("tenantId", searchRequest.getTenantId());
        }

        if (searchRequest.getCode() != null) {
            addAnd(params);
            params.append("code =:code");
            paramValues.put("code", searchRequest.getCode());
        }

        if (searchRequest.getShiftTypeCode() != null) {
            addAnd(params);
            params.append("shiftType =:shiftType");
            paramValues.put("shiftType", searchRequest.getShiftTypeCode());
        }

        if (searchRequest.getDepartmentCode() != null) {
            addAnd(params);
            params.append("department =:department");
            paramValues.put("department", searchRequest.getDepartmentCode());
        }

        if (searchRequest.getDesignationCode() != null) {
            addAnd(params);
            params.append("designation =:designation");
            paramValues.put("designation", searchRequest.getDesignationCode());
        }

        Pagination<Shift> page = new Pagination<>();
        if (searchRequest.getOffset() != null)
            page.setOffset(searchRequest.getOffset());
        if (searchRequest.getPageSize() != null)
            page.setPageSize(searchRequest.getPageSize());

        if (params.length() > 0)
            searchQuery = searchQuery.replace(":condition", " where " + params.toString());
        else

            searchQuery = searchQuery.replace(":condition", "");

        searchQuery = searchQuery.replace(":orderby", orderBy);

        page = (Pagination<Shift>) getPagination(searchQuery, page, paramValues);
        searchQuery = searchQuery + " :pagination";

        searchQuery = searchQuery.replace(":pagination",
                "limit " + page.getPageSize() + " offset " + page.getOffset() * page.getPageSize());

        final BeanPropertyRowMapper row = new BeanPropertyRowMapper(ShiftEntity.class);

        final List<Shift> shiftList = new ArrayList<>();

        final List<ShiftEntity> shiftEntities = namedParameterJdbcTemplate.query(searchQuery.toString(),
                paramValues, row);
        StringBuffer shiftCodes = new StringBuffer();

        for (final ShiftEntity shiftEntity : shiftEntities) {
            shiftList.add(shiftEntity.toDomain());

            if (shiftCodes.length() >= 1)
                shiftCodes.append(",");

            shiftCodes.append(shiftEntity.getCode());
        }

        if (shiftList != null && !shiftList.isEmpty()) {
            populateShiftTypes(shiftList);
            populateDepartments(shiftList);
        }

        page.setPagedData(shiftList);

        return page;
    }

    private void populateShiftTypes(List<Shift> shiftList) {
        Map<String, ShiftType> shiftTypeMap = new HashMap<>();
        String tenantId = null;

        if (shiftList != null && !shiftList.isEmpty())
            tenantId = shiftList.get(0).getTenantId();

        List<ShiftType> shiftTypes = shiftTypeService.getAll(tenantId, new RequestInfo());

        for (ShiftType vt : shiftTypes) {
            shiftTypeMap.put(vt.getCode(), vt);
        }

        for (Shift shift : shiftList) {

            if (shift.getShiftType() != null && shift.getShiftType().getCode() != null
                    && !shift.getShiftType().getCode().isEmpty()) {

                shift.setShiftType(shiftTypeMap.get(shift.getShiftType().getCode()));
            }

        }
    }

    private void populateDepartments(List<Shift> shiftList) {
        Map<String, Department> departmentMap = new HashMap<>();
        String tenantId = null;

        if (shiftList != null && !shiftList.isEmpty())
            tenantId = shiftList.get(0).getTenantId();

        List<Department> departments = departmentService.getAll(tenantId, new RequestInfo());

        for (Department vt : departments) {
            departmentMap.put(vt.getCode(), vt);
        }

        for (Shift shift : shiftList) {

            if (shift.getDepartment() != null && shift.getDepartment().getCode() != null
                    && !shift.getDepartment().getCode().isEmpty()) {

                shift.setDepartment(departmentMap.get(shift.getDepartment().getCode()));
            }

        }
    }
}