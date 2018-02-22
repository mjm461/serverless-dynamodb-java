package com.game.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
public abstract class AbstractPagingController<ID extends Serializable, E, R extends PagingAndSortingRepository<E, ID>> {

    final protected R repository;

    protected AbstractPagingController(R repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public E get(@PathVariable("id") ID id) {
        return repository.findOne(id);
    }

    protected Pageable makePageable(Integer page, Integer perPage) {
        page    =    page == null ||    page < 0 ? 0  : page;
        perPage = perPage == null || perPage < 1 ? 15 : perPage;
        return new PageRequest(page, perPage);
    }
}