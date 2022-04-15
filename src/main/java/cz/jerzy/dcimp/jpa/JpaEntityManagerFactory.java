package cz.jerzy.dcimp.jpa;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

//https://www.baeldung.com/java-bootstrap-jpa
@Slf4j
public class JpaEntityManagerFactory {

    private Path dbFile;
    private Class[] entityClasses;

    public JpaEntityManagerFactory(Path dbFile, Class[] entityClasses) {
        this.dbFile = dbFile;
        this.entityClasses = entityClasses;
    }

    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    protected EntityManagerFactory getEntityManagerFactory() {
        PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(getClass().getSimpleName());
        Map<String, Object> configuration = new HashMap<>();
        return new EntityManagerFactoryBuilderImpl(
                new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
                .build();
    }

    protected HibernatePersistenceUnitInfo getPersistenceUnitInfo(String name) {
        return new HibernatePersistenceUnitInfo(name, getEntityClassNames(), getProperties());
    }

    protected List<String> getEntityClassNames() {
        return Arrays.asList(getEntities())
                .stream()
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.connection.url", getConnectionUrl());
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.pool_size", "1");
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.dbcp.initialSize", "5");
        properties.put("hibernate.dbcp.maxTotal", "20");
        properties.put("hibernate.dbcp.maxIdle", "10");
        properties.put("hibernate.dbcp.minIdle", "5");
        properties.put("hibernate.dbcp.maxWaitMillis", "-1");
        properties.put("show_sql", "true");
        return properties;
    }

    private String getConnectionUrl() {
        String url = "jdbc:h2:file:" + dbFile.toString().replace("\\", "/");
        log.info("H2 URL: {}.mv.db", url);
        return url;
    }

    protected Class[] getEntities() {
        return entityClasses;
    }

    protected DataSource getJdbcDataSource() {
        JdbcDataSource mysqlDataSource = new JdbcDataSource();
        mysqlDataSource.setURL(getConnectionUrl());
        mysqlDataSource.setUser("sa");
        mysqlDataSource.setPassword("");
        return mysqlDataSource;
    }
}
