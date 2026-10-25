package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.Note;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.NoteRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NoteController {

    private final NoteRepository noteRepository;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // ================= VIEW NOTES =================

    @GetMapping("/notes")
    public String notes(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        List<Note> notes = noteRepository.findByUserEmailOrderByPinnedDescIdDesc(
                user.getEmail());

        model.addAttribute("notes", notes);

        return "notes";
    }

    // ================= ADD NOTE =================

    @PostMapping("/notes/add")
    public String addNote(
            @RequestParam String title,
            @RequestParam(required = false) String content,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Note note = new Note();

        note.setTitle(title);
        note.setContent(content);
        note.setUserEmail(user.getEmail());
        note.setPinned(false);

        noteRepository.save(note);

        return "redirect:/notes";
    }

    // ================= EDIT NOTE =================

    @GetMapping("/notes/edit")
    public String editNote(
            @RequestParam Long id,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Note note = noteRepository.findById(id).orElse(null);

        if (note == null ||
                !note.getUserEmail().equals(user.getEmail())) {

            return "redirect:/notes";
        }

        model.addAttribute("note", note);

        return "edit-note";
    }

    // ================= UPDATE NOTE =================

    @PostMapping("/notes/update")
    public String updateNote(
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam String content,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Note note = noteRepository.findById(id).orElse(null);

        if (note != null &&
                note.getUserEmail().equals(user.getEmail())) {

            note.setTitle(title);
            note.setContent(content);

            noteRepository.save(note);
        }

        return "redirect:/notes";
    }

    // ================= PIN / UNPIN NOTE =================

    @GetMapping("/notes/pin")
    public String pinNote(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Note note = noteRepository.findById(id).orElse(null);

        if (note != null &&
                note.getUserEmail().equals(user.getEmail())) {

            note.setPinned(!note.isPinned());

            noteRepository.save(note);
        }

        return "redirect:/notes";
    }

    // ================= DELETE NOTE =================

    @GetMapping("/notes/delete")
    public String deleteNote(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Note note = noteRepository.findById(id).orElse(null);

        if (note != null &&
                note.getUserEmail().equals(user.getEmail())) {

            noteRepository.delete(note);
        }

        return "redirect:/notes";
    }
}