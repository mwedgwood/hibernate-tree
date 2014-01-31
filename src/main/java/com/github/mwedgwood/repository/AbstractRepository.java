package com.github.mwedgwood.repository;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.cache.internal.StandardQueryCache;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;

import java.util.List;

@SuppressWarnings({"unchecked"})
public abstract class AbstractRepository<T> implements Repository<T> {

    private String queryCacheRegion;

    public AbstractRepository() {
        queryCacheRegion = StandardQueryCache.class.getName();
    }

    protected abstract Session getCurrentSession();

    protected abstract Class<T> getPersistentClass();

    protected Example createExample(T example) {
        return Example.create(example).excludeZeroes();
    }

    @Override
    public T findById(Integer id) {
        return (T) getCurrentSession().get(getPersistentClass(), id);
    }

    @Override
    public RepositoryResult<T> findAll() {
        Criteria criteria = createCriteria(true);
        return createRepositoryResult(criteria);
    }

    @Override
    public RepositoryResult<T> findByExample(T example) {
        Criteria criteria = createCriteria(true, createExample(example));
        return createRepositoryResult(criteria);
    }

    @Override
    public void save(T entity) {
        getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    @Override
    public void update(T entity) {
        getCurrentSession().merge(entity);
    }

    protected List<T> findByCriteria(Criterion... criterion) {
        return createCriteria(criterion).list();
    }

    protected Criteria createCriteria(Criterion... criterion) {
        return createCriteria(true, criterion);
    }

    protected Criteria createCriteria(boolean cacheable, Criterion... criterion) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion crit : criterion) {
            criteria.add(crit);
        }
        criteria.setCacheable(cacheable);
        criteria.setCacheRegion(queryCacheRegion);
        criteria.setReadOnly(getCurrentSession().isDefaultReadOnly());
        return criteria;
    }

    protected RepositoryResult<T> createRepositoryResult(Criteria criteria) {
        return new RepositoryResultImpl<>(criteria);
    }

    private static class RepositoryResultImpl<T> implements RepositoryResult<T> {

        private final Criteria criteria;

        RepositoryResultImpl(Criteria criteria) {
            this.criteria = criteria;
        }

        @Override
        public RepositoryResult<T> limit(int page, int perPage) {
            int start = (page - 1) * perPage;
            criteria.setFirstResult(start).setMaxResults(perPage);
            return this;
        }

        @Override
        public RepositoryResult<T> orderBy(Order... orders) {
            for (Order order : orders) {
                criteria.addOrder(order);
            }
            return this;
        }

        @Override
        public List<T> list() {
            return criteria.list();
        }

    }

}
