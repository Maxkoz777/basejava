package com.urise.webapp.storage;

import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.ContactType;
import com.urise.webapp.model.Link;
import com.urise.webapp.model.Resume;
import com.urise.webapp.sql.SqlHelper;

import java.sql.*;
import java.util.*;

public class SqlStorage implements Storage {
    public final SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        sqlHelper = new SqlHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));
    }

    @Override
    public void clear() {
        sqlHelper.execute("delete from resume");
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute("select * from resume r \n" +
                        "left join contact c on r.uuid = c.resume_uuid " +
                        "where uuid=? ",
                ps -> {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new NotExistStorageException(uuid);
                    }
                    Resume resume = new Resume(uuid, rs.getString("full_name"));
                    do {
                        addContact(rs, resume);
                    } while (rs.next());
                    return resume;
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
            insertContact(connection, r);
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
            insertContact(connection, r);
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
        return sqlHelper.execute("select * from resume r left join contact c on r.uuid = c.resume_uuid order by full_name,uuid",
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    Map<String, Resume> map = new LinkedHashMap<>();
                    while (rs.next()) {
                        String uuid = rs.getString("uuid");
                        Resume resume = map.get(uuid);
                        if (resume == null) {
                            resume = new Resume(uuid, rs.getString("full_name"));
                            map.put(uuid, resume);
                        }
                        addContact(rs, resume);
                    }
                    return new ArrayList<>(map.values());
                });
    }


    @Override
    public int size() {
        return sqlHelper.execute("select count(*) from resume", st -> {
            ResultSet rs = st.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        });
    }

    private void insertContact(Connection connection, Resume resume) throws SQLException {
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

    private void deleteContacts(Connection connection, Resume r) {
        sqlHelper.execute("delete from contact where resume_uuid = ?", ps -> {
            ps.setString(1, r.getUuid());
            ps.execute();
            return null;
        });
    }

    private void addContact(ResultSet rs, Resume resume) throws SQLException {
        String value = rs.getString("value");
        if (value != null) {
            resume.addContact(ContactType.valueOf(rs.getString("type")), value);
        }
    }
}
