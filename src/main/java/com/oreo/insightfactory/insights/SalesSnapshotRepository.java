package com.oreo.insightfactory.insights;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class SalesSnapshotRepository {

    private final JdbcTemplate jdbcTemplate;

    public SalesSnapshotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SalesSnapshot loadSnapshot() {
        try {
            Long totalSales = jdbcTemplate.queryForObject("select count(*) from sales", Long.class);
            Long totalUnits = jdbcTemplate.queryForObject("select coalesce(sum(quantity), 0) from sales", Long.class);
            BigDecimal totalRevenue = jdbcTemplate.queryForObject(
                    "select coalesce(sum(quantity * unit_price), 0) from sales",
                    BigDecimal.class
            );

            return new SalesSnapshot(
                    valueOrZero(totalSales),
                    valueOrZero(totalUnits),
                    totalRevenue == null ? BigDecimal.ZERO : totalRevenue,
                    groupBy("branch"),
                    groupBy("sku")
            );
        } catch (DataAccessException exception) {
            return SalesSnapshot.empty();
        }
    }

    private List<GroupSnapshot> groupBy(String column) {
        String sql = """
                select %s as name,
                       coalesce(sum(quantity), 0) as units,
                       coalesce(sum(quantity * unit_price), 0) as revenue
                from sales
                group by %s
                order by revenue desc
                """.formatted(column, column);

        return jdbcTemplate.query(sql, (rs, rowNum) -> new GroupSnapshot(
                rs.getString("name"),
                rs.getLong("units"),
                rs.getBigDecimal("revenue")
        ));
    }

    private long valueOrZero(Long value) {
        return value == null ? 0 : value;
    }
}
