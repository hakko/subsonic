package net.sourceforge.subsonic.backend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Abstract superclass for all DAO's.
 *
 * @author Sindre Mehus
 */
public class AbstractDao {
    private DaoHelper daoHelper;

    /**
     * Returns a JDBC template for performing database operations.
     * @return A JDBC template.
     */
    public JdbcTemplate getJdbcTemplate() {
        return daoHelper.getJdbcTemplate();
    }

    protected String questionMarks(String columns) {
        int count = columns.split(", ").length;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buf.append('?');
            if (i < count - 1) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    protected int update(String sql, Object... args) {
        return getJdbcTemplate().update(sql, args);
    }

    protected <T> List<T> query(String sql, RowMapper rowMapper, Object... args) {
        return getJdbcTemplate().query(sql, args, rowMapper);
    }

    protected <T> T queryOne(String sql, RowMapper rowMapper, Object... args) {
        List<T> result = query(sql, rowMapper, args);
        return result.isEmpty() ? null : result.get(0);
    }

    protected Integer queryForInt(String sql, Integer defaultValue, Object... args) {
        List<Integer> result = getJdbcTemplate().queryForList(sql, args, Integer.class);
        return result.isEmpty() ? defaultValue : result.get(0) == null ? defaultValue : result.get(0);
    }

    public void setDaoHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    protected static class MoneyRowMapper implements ParameterizedRowMapper<Money> {
        public Money mapRow(ResultSet rs, int rowNum) throws SQLException {
            CurrencyUnit currency = CurrencyUnit.of(rs.getString(2));
            return Money.of(currency, rs.getDouble(1));
        }
    }
}
