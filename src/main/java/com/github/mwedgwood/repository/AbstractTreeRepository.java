package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.tree.Tree;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.DiscriminatorValue;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractTreeRepository<T extends Tree> extends AbstractRepository<T> implements TreeRepository<T> {

    @Override
    public T findByIdForDepth(Integer id, Integer depth) {
        T treeById = super.findById(id);

        return initializeToDepth(depth, treeById);
    }

    @Override
    public T findRoot() {
        return (T) createCriteria(Restrictions.isNull("parent"))
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .uniqueResult();
    }

    @Override
    public List<T> findByName(String name) {
        return createCriteria(Restrictions.eq("element.name", name)).list();
    }

    @Override
    public void save(T entity) {
        if (entity.getParent() == null && findRoot() != null) {
            throw new ConstraintViolationException(String.format("Can not create more than one root %s", getPersistentClass()), null, null);
        }
        getCurrentSession().save(entity);
    }

    @Override
    public void delete(T entity) {
        T parent = (T) entity.getParent();
        if (parent == null) {
            throw new ConstraintViolationException(String.format("Can not delete root %s", getPersistentClass()), null, null);
        }
        parent.removeChildTree(entity);
        getCurrentSession().delete(entity);
        update(parent);
    }


    public T findEntireTree() {
        Integer rootId = (Integer) getCurrentSession().getNamedQuery("findRootNode").setCacheable(true)
                .setParameter("aClass", getPersistentClass().getAnnotation(DiscriminatorValue.class).value())
                .uniqueResult();

        getCurrentSession().getNamedQuery("findAllNodesWithTheirChildren").setCacheable(true)
                .setParameter("aClass", getPersistentClass().getAnnotation(DiscriminatorValue.class).value())
                .list();

        return (T) getCurrentSession().load(getPersistentClass(), rootId);
    }

    T initializeToDepth(int depth, T tree) {
        if (tree == null) return null;
        initializeToDepth(0, depth, tree);
        return tree;
    }

    private void initializeToDepth(int currentDepth, int depth, T tree) {
        if (currentDepth++ == depth) return;

        for (T child : (List<T>) tree.getChildren()) {
            initializeToDepth(currentDepth, depth, child);
        }
    }

}
