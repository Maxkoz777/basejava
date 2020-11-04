package com.urise.webapp.storage;

import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.*;
import com.urise.webapp.sql.SqlHelper;
import com.urise.webapp.util.JsonParser;

import java.sql.*;
import java.util.*;

public class SqlStorage implements Storage {
    public final SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        sqlHelper = new SqlHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));
    }

    @Override
    public void clear() {
        sqlHelper.execute("delete from resume");
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.transactionalExecute(connection -> {
            Resume r;
            try(PreparedStatement ps = connection.prepareStatement("select * from resume where uuid=?")) {
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()){
                    throw new NotExistStorageException(uuid);
                }
                r = new Resume(uuid, rs.getString("full_name"));
            }
            try(PreparedStatement ps = connection.prepareStatement("select * from contact where resume_uuid = ?")){
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    addContact(rs, r);
                }
            }

            try(PreparedStatement ps = connection.prepareStatement("select * from section where resume_uuid = ?")){
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    addSection(rs, r);
                }
            }

            return r;
        });
    }

    @Override
    public void update(Resume r) {
        sqlHelper.transactionalExecute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("update resume set full_name = ? where uuid = ?")) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(r.getUuid());
                }
            }
            deleteContacts(connection, r);
            deleteSections(connection, r);
            insertContacts(connection, r);
            insertSections(connection, r);
            return null;
        });
    }

    @Override
    public void save(Resume r) {
        sqlHelper.transactionalExecute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("insert into resume (uuid, full_name) values (?,?)")) {
                ps.setString(1, r.getUuid());
                ps.setString(2, r.getFullName());
                ps.execute();
            }
            insertContacts(connection, r);
            insertSections(connection, r);
            return null;
        });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.execute("delete from resume where uuid=?", ps -> {
            ps.setString(1, uuid);
            if (ps.executeUpdate() == 0) {
                throw new NotExistStorageException(uuid);
            }
            return null;
        });
    }

    @Override
    public List<Resume> getAllSorted() {
        return sqlHelper.transactionalExecute( connection -> {
           Map<String, Resume> resumes = new LinkedHashMap<>();

           try(PreparedStatement ps = connection.prepareStatement("select * from resume order by full_name, uuid")){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    String uuid = rs.getString("uuid");
                    resumes.put(uuid, new Resume(uuid, rs.getString("full_name")));
                }
           }

            try(PreparedStatement ps = connection.prepareStatement("select * from contact")){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    Resume r = resumes.get(rs.getString("resume_uuid"));
                    addContact(rs, r);
                }
            }

            try(PreparedStatement ps = connection.prepareStatement("select * from section")){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    Resume r = resumes.get(rs.getString("resume_uuid"));
                    addSection(rs, r);
                }
            }

            return new ArrayList<>(resumes.values());
        });
    }


    @Override
    public int size() {
        return sqlHelper.execute("select count(*) from resume", st -> {
            ResultSet rs = st.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        });
    }

    private void insertContacts(Connection connection, Resume resume) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("insert into contact (type, value, resume_uuid) values (?, ?, ?)")) {
            for (Map.Entry<ContactType, String> map : resume.getContacts().entrySet()) {
                ps.setString(1, map.getKey().name());
                ps.setString(2, map.getValue());
                ps.setString(3, resume.getUuid());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertSections(Connection connection, Resume resume) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("insert into section (resume_uuid, type, content) values (?, ?, ?)")) {
            for (Map.Entry<SectionType, Section> map : resume.getSections().entrySet()) {
                ps.setString(1, resume.getUuid());
                ps.setString(2, map.getKey().name());
                Section section = map.getValue();
                ps.setString(3, JsonParser.write(section, Section.class));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteContacts(Connection connection, Resume r) throws SQLException {
        deleteAttributes(connection, r, "delete from contact where resume_uuid = ?");
    }

    private void deleteSections(Connection connection, Resume r) throws SQLException {
        deleteAttributes(connection, r, "delete from section where resume_uuid = ?");
    }

    private void deleteAttributes(Connection connection, Resume r, String sql) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, r.getUuid());
            ps.execute();
        }
    }

    private void addContact(ResultSet rs, Resume resume) throws SQLException {
        String value = rs.getString("value");
        if (value != null) {
            resume.addContact(ContactType.valueOf(rs.getString("type")), value);
        }
    }

    private void addSection(ResultSet rs, Resume r) throws SQLException {
        String content = rs.getString("content");
        if (content != null){
            SectionType type = SectionType.valueOf(rs.getString("type"));
            r.addSection(type, JsonParser.read(content, Section.class));
        }
    }
}
