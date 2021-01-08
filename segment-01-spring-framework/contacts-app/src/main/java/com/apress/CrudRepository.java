package com.apress;

public interface CrudRepository<T,ID>{

    T save(final T contact);
    T deleteById(final ID id);
    Iterable<T> findAll();
    T findById(final ID id);

}
