package org.egov.swm.persistence.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.swm.domain.model.BinDetails;
import org.egov.swm.domain.model.BinDetailsSearch;
import org.egov.swm.persistence.entity.BinDetailsEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BinDetailsJdbcRepository extends JdbcRepository {

    public static final String TABLE_NAME = "egswm_bindetails";

    public Boolean uniqueCheck(final String tenantId, final String fieldName, final String fieldValue,
            final String uniqueFieldName,
            final String uniqueFieldValue) {

        return uniqueCheck(TABLE_NAME, tenantId, fieldName, fieldValue, uniqueFieldName, uniqueFieldValue);
    }

    @Transactional
    public void delete(final String tenantId, final String collectionPoint) {
        delete(TABLE_NAME, tenantId, "collectionPoint", collectionPoint);
    }

    public List<BinDetails> search(final BinDetailsSearch searchRequest) {

        String searchQuery = "select * from " + TABLE_NAME + " :condition ";

        final Map<String, Object> paramValues = new HashMap<>();
        final StringBuffer params = new StringBuffer();

        if (searchRequest.getTenantId() != null) {
            addAnd(params);
            params.append("tenantId =:tenantId");
            paramValues.put("tenantId", searchRequest.getTenantId());
        }

        if (searchRequest.getId() != null) {
            addAnd(params);
            params.append("id =:id");
            paramValues.put("id", searchRequest.getId());
        }

        if (searchRequest.getRfidAssigned() != null) {
            addAnd(params);
            params.append("rfidAssigned =:rfidAssigned");
            paramValues.put("rfidAssigned", searchRequest.getRfidAssigned());
        }

        if (searchRequest.getAssetCode() != null) {
            addAnd(params);
            params.append("asset =:asset");
            paramValues.put("asset", searchRequest.getAssetCode());
        }

        if (searchRequest.getCollectionPoint() != null) {
            addAnd(params);
            params.append("collectionPoint =:collectionPoint");
            paramValues.put("collectionPoint", searchRequest.getCollectionPoint());
        }

        if (searchRequest.getCollectionPoints() != null) {
            addAnd(params);
            params.append("collectionPoint in (:collectionPoints)");
            paramValues.put("collectionPoints", new ArrayList<>(Arrays.asList(searchRequest.getCollectionPoints().split(","))));
        }

        if (searchRequest.getCollectionPoint() != null) {
            addAnd(params);
            params.append("collectionPoint =:collectionPoint");
            paramValues.put("collectionPoint", searchRequest.getCollectionPoint());
        }

        if (searchRequest.getRfid() != null) {
            addAnd(params);
            params.append("rfid =:rfid");
            paramValues.put("rfid", searchRequest.getRfid());
        }

        if (params.length() > 0)
            searchQuery = searchQuery.replace(":condition", " where " + params.toString());
        else

            searchQuery = searchQuery.replace(":condition", "");

        final BeanPropertyRowMapper row = new BeanPropertyRowMapper(BinDetailsEntity.class);

        final List<BinDetailsEntity> entityList = namedParameterJdbcTemplate.query(searchQuery.toString(), paramValues, row);

        final List<BinDetails> resultList = new ArrayList<>();

        if (entityList != null && !entityList.isEmpty()) {

            for (final BinDetailsEntity bde : entityList) {

                resultList.add(bde.toDomain());
            }

        }

        return resultList;
    }

}