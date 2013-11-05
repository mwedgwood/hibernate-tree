package com.github.mwedgwood.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class UnitOfWork<T> {

    public abstract T unitOfWork(final Session session) throws Throwable;

    public final T execute(final Session session, boolean readOnly) throws Throwable {

        if (session.getTransaction().isActive()) {
            return unitOfWork(session);
        } else {
            Transaction transaction = session.beginTransaction();
            session.setDefaultReadOnly(readOnly);
            try {
                T result = unitOfWork(session);
                transaction.commit();
                return result;
            } finally {
                if (!transaction.wasCommitted()) {
                    transaction.rollback();
                }
            }
        }
    }
}
