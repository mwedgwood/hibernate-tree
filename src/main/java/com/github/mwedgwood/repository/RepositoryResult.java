package com.github.mwedgwood.repository;

import org.hibernate.criterion.Order;

import java.util.List;
import java.util.Set;

public interface RepositoryResult<T> {

    RepositoryResult<T> limit(int page, int perPage);

    RepositoryResult<T> orderBy(Set<Order> orders);

    List<T> list();
}
