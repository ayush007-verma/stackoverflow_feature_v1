package com.clone.stackoverflow.Repository;

import com.clone.stackoverflow.model.Answer;
import com.clone.stackoverflow.model.AnswerVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote, Long>{



}