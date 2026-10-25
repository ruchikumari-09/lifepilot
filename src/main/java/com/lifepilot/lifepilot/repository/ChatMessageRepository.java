package com.lifepilot.lifepilot.repository;

import com.lifepilot.lifepilot.entity.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository
                extends JpaRepository<ChatMessage, Long> {

        List<ChatMessage> findByUserEmailOrderByCreatedAtAsc(
                        String userEmail);

        @Transactional
        void deleteByUserEmail(
                        String userEmail);
}