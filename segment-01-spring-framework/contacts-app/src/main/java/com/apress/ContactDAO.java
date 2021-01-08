package com.apress;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContactDAO implements CrudRepository<Contact, String> {
    private final String SQL_INSERT = "insert into contact (name,email,phone) values (?,?,?)";
    private final String SQL_UPDATE = "update contact set name = ?, email = ?, phone = ?)";
    private final String SQL_DELETE_BY_ID = "delete from contact where email = ?";
    private final String SQL_QUERY_ALL = "select name,email,phone from contact";
    private final String SQL_QUERY_BY_ID = "select name,email,phone from contact where email = ?";

    private JdbcTemplate jdbcTemplate;

    private RowMapper  rowMapper = (ResultSet rs, int row)-> {
        Contact contact = new Contact();
        contact.setName(rs.getString(1));
        contact.setEmail(rs.getString(2));
        contact.setPhone(rs.getString(3));
        return  contact;
    };

    public ContactDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Contact save(Contact contact) {

        Contact oldContact = this.findById(contact.getEmail());

        if(oldContact == null && doContactAction(SQL_INSERT, contact))
           return contact;

        if(oldContact != null && doContactAction(SQL_UPDATE, oldContact))
           return oldContact;

        return null;
    }

    public Contact deleteById(String s) {
        Contact oldContact = this.findById(s);

        Boolean result = this.jdbcTemplate.execute(SQL_DELETE_BY_ID,(PreparedStatement preparedStatement) -> {
            preparedStatement.setString(1,s);
            return preparedStatement.execute();
        });

        if (result)
            return  oldContact;

        return null;
    }

    public Iterable<Contact> findAll() {
        return this.jdbcTemplate.query(SQL_QUERY_ALL, rowMapper);
    }

    public Contact findById(String email) {
        return (Contact) this.jdbcTemplate.queryForObject(SQL_QUERY_BY_ID,rowMapper, new Object[]{email});
    }

    private Boolean doContactAction(String sql, Contact contact) {
        return this.jdbcTemplate.execute(sql, (PreparedStatement preparedStatement) -> {
            preparedStatement.setString(1, contact.getName());
            preparedStatement.setString(2, contact.getEmail());
            preparedStatement.setString(3, contact.getPhone());
            return preparedStatement.execute();
        });
    }
}
