package com.turkcell.reactive.meetup.repository;


import com.turkcell.reactive.meetup.model.Comment;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommentRepository extends PagingAndSortingRepository<Comment, String> {
}
