package com.tabulaw.service.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.tabulaw.model.User;

public class UserRowMapper extends ModelRowMapper implements ParameterizedRowMapper<User> {
	public User mapRow(ResultSet rs, int rownum) throws SQLException {
        return loadUser(rs);
    }
}
