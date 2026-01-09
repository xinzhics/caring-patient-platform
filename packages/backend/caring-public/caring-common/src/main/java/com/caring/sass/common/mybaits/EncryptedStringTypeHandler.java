package com.caring.sass.common.mybaits;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class EncryptedStringTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> clazz;

    public EncryptedStringTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            String encryptedValue = EncryptionUtil.encrypt(parameter.toString());
            ps.setString(i, encryptedValue);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        String value = rs.getString(columnName);
        if (value != null) {
            try {
                String decrypt = EncryptionUtil.decrypt(value);
                return (T) decrypt;
            } catch (Exception e) {
                throw new RuntimeException("Decryption failed", e);
            }
        }
        return null;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (value != null) {
            try {
                return (T) EncryptionUtil.decrypt(value);
            } catch (Exception e) {
                throw new RuntimeException("Decryption failed", e);
            }
        }
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (value != null) {
            try {
                return (T) EncryptionUtil.decrypt(value);
            } catch (Exception e) {
                throw new RuntimeException("Decryption failed", e);
            }
        }
        return null;
    }

}