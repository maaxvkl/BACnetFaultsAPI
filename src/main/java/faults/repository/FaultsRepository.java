package faults.repository;

import faults.model.Fault;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;
import java.util.List;

@Repository
@AllArgsConstructor
public class FaultsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public int addFault(Fault fault) {
        String sql = """
               INSERT INTO faults (
               device_ip,
               data_type,
               object_name,
               object_description,
               event_state,
               properties
               ) VALUES (?,?,?,?,?,?::jsonb)
               """;
        String propertiesJson = objectMapper.writeValueAsString(fault.getProperties());
        return jdbcTemplate.update(sql, fault.getDeviceIp(), fault.getObjectType(), fault.getObjectName(), fault.getObjectDescription(),
                                    fault.getEventState(), propertiesJson);
    }

    public List<String> findAllDeviceIps() {
        String sql = """
                SELECT DISTINCT device_ip
                FROM faults
                ORDER BY device_ip
                """ ;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("device_ip") );
    }

    public List<String> findByDataType(String dataType) {
        String sql = """
                SELECT *
                FROM faults
                WHERE data_type = ?
                """ ;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("object_name"), dataType );
    }

    public int deleteAll() {
        String sql = """
                DELETE FROM faults
                """;
        return jdbcTemplate.update(sql);
    }
}


