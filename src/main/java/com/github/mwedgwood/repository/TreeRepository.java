package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.tree.Tree;

import java.util.List;

public interface TreeRepository<T extends Tree> extends Repository<T> {

    List<T> findByName(String name);

    T findRoot();

    T findByIdForDepth(Integer id, Integer depth);
}
