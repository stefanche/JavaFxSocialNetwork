package com.example.demo.Repository;

import com.example.demo.domain.Entity;
import com.example.demo.domain.User;
import com.example.demo.utils.Page;
import com.example.demo.utils.Pageable;


public interface PagingRepo<ID , E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable, User loggedIn);
}
