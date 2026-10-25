package com.lifepilot.lifepilot.repository;

import com.lifepilot.lifepilot.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserEmailOrderByPinnedDescIdDesc(String userEmail);

}