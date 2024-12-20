package com.example.demo.Repository;

import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;
import com.example.demo.utils.Page;
import com.example.demo.utils.Pageable;

import java.util.List;

public interface PageableFriendshipView extends PagingRepo<Integer, FriendshipsView> {
        Page<FriendshipsView> findAllOnPage(Pageable pageable, User loggdIn);
}
