package com.github.old.dog.star.boot.data.access.api;

/**
 * @JavaDoc is required [todo]
 */
public interface DataAccess<POJO, RECORD, ID, REPO> {
    <O> O atomicOperation(DataAccessOperation<O, REPO> operation);

    <O> O simpleOperation(DataAccessOperation<O, REPO> operation);

    <O> O templateOperation(DataAccessTemplate<POJO, RECORD, ID, REPO> operation);

}
