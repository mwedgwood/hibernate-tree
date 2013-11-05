package com.github.mwedgwood.service;

import org.hibernate.SessionFactory;

public interface PersistenceService {

    SessionFactory getSessionFactory();

    void initialize();

    void destroy();
}
