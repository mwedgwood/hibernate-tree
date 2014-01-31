package com.github.mwedgwood.service;

import com.github.mwedgwood.model.tree.Tree;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.reflections.Reflections;

import javax.persistence.Entity;

import static org.hibernate.cfg.AvailableSettings.*;

public class TestPersistenceServiceImpl implements PersistenceService {

    private SessionFactory _sessionFactory;

    private TestPersistenceServiceImpl() {
        initialize();
    }

    private static class SingletonHolder {
        private static final TestPersistenceServiceImpl INSTANCE = new TestPersistenceServiceImpl();
    }

    public static TestPersistenceServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return _sessionFactory;
    }

    @Override
    public void initialize() {
        Configuration configuration = new Configuration();
        configuration.setProperty(Environment.CONNECTION_PROVIDER, TestConnectionProvider.class.getName());
        configuration.setProperty(DIALECT, H2Dialect.class.getName());
        configuration.setProperty(HBM2DDL_AUTO, "create-drop");
        configuration.setProperty(USE_SECOND_LEVEL_CACHE, Boolean.FALSE.toString());
        configuration.setProperty(USE_QUERY_CACHE, Boolean.FALSE.toString());
        configuration.setProperty(SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(FORMAT_SQL, Boolean.TRUE.toString());
        addAnnotatedClasses(configuration);

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        _sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    @Override
    public void destroy() {
    }

    protected Configuration addAnnotatedClasses(Configuration configuration) {
        for (Class<?> aClass : new Reflections(Tree.class.getPackage().getName()).getTypesAnnotatedWith(Entity.class)) {
            // don't load classes that have generic type parameters as hibernate can not deal with them unless used as superclasses
            if (aClass.getTypeParameters().length == 0) configuration.addAnnotatedClass(aClass);
        }
        return configuration;
    }

}
