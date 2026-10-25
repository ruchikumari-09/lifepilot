package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.ChatMessage;
import com.lifepilot.lifepilot.entity.Task;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.ChatMessageRepository;
import com.lifepilot.lifepilot.repository.TaskRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AIAssistantController {

        private final GoogleGenAiChatModel chatModel;
        private final TaskRepository taskRepository;
        private final ChatMessageRepository chatMessageRepository;

        public AIAssistantController(
                        GoogleGenAiChatModel chatModel,
                        TaskRepository taskRepository,
                        ChatMessageRepository chatMessageRepository) {

                this.chatModel = chatModel;
                this.taskRepository = taskRepository;
                this.chatMessageRepository = chatMessageRepository;
        }

        // =========================================================
        // AI ASSISTANT PAGE
        // /ai AND /ai-assistant BOTH WORK
        // =========================================================

        @GetMapping({ "/ai", "/ai-assistant" })
        public String aiAssistant(
                        HttpSession session,
                        Model model) {

                User user = (User) session.getAttribute("loggedInUser");

                if (user == null) {
                        return "redirect:/login";
                }

                // Load saved chat history
                List<ChatMessage> chatHistory = chatMessageRepository
                                .findByUserEmailOrderByCreatedAtAsc(
                                                user.getEmail());

                model.addAttribute("chatHistory", chatHistory);
                model.addAttribute("user", user);

                return "ai-assistant";
        }

        // =========================================================
        // ASK AI
        // =========================================================

        @PostMapping("/ai-assistant/ask")
        @ResponseBody
        public String askAI(
                        @RequestParam String message,
                        HttpSession session) {

                User user = (User) session.getAttribute("loggedInUser");

                if (user == null) {
                        return "Please login first.";
                }

                if (message == null || message.trim().isEmpty()) {
                        return "Please enter a question.";
                }

                try {

                        // =================================================
                        // GET PENDING TASKS
                        // =================================================

                        List<Task> pendingTasks = taskRepository
                                        .findByUserEmailAndCompletedFalseOrderByTaskDateAscTaskTimeAsc(
                                                        user.getEmail());

                        // =================================================
                        // PREPARE TASK INFORMATION
                        // =================================================

                        StringBuilder taskInfo = new StringBuilder();

                        if (pendingTasks.isEmpty()) {

                                taskInfo.append(
                                                "The user currently has no pending tasks.");

                        } else {

                                taskInfo.append(
                                                "The user currently has "
                                                                + pendingTasks.size()
                                                                + " pending task(s):\n\n");

                                int count = 1;

                                for (Task task : pendingTasks) {

                                        taskInfo.append(count)
                                                        .append(". ");

                                        taskInfo.append("Title: ")
                                                        .append(task.getTitle());

                                        if (task.getDescription() != null &&
                                                        !task.getDescription().trim().isEmpty()) {

                                                taskInfo.append(", Description: ")
                                                                .append(task.getDescription());
                                        }

                                        if (task.getTaskDate() != null) {

                                                taskInfo.append(", Date: ")
                                                                .append(task.getTaskDate());
                                        }

                                        if (task.getTaskTime() != null) {

                                                taskInfo.append(", Time: ")
                                                                .append(task.getTaskTime());
                                        }

                                        if (task.getPriority() != null) {

                                                taskInfo.append(", Priority: ")
                                                                .append(task.getPriority());
                                        }

                                        taskInfo.append("\n");

                                        count++;
                                }
                        }

                        // =================================================
                        // AI PROMPT
                        // =================================================

                        String prompt = "You are LifePilot AI, "
                                        + "a helpful personal life assistant.\n\n"

                                        + "Help the user with:\n"
                                        + "- Daily planning\n"
                                        + "- Tasks\n"
                                        + "- Goals\n"
                                        + "- Habits\n"
                                        + "- Productivity\n"
                                        + "- General questions\n\n"

                                        + "The following task information "
                                        + "comes directly from the user's "
                                        + "LifePilot database.\n\n"

                                        + "USER'S CURRENT PENDING TASKS:\n"
                                        + taskInfo

                                        + "\n\nUSER QUESTION:\n"
                                        + message

                                        + "\n\n"
                                        + "Answer clearly and naturally.\n"
                                        + "If the user asks about pending work, "
                                        + "use the actual task information.\n"
                                        + "Do not claim that you cannot access "
                                        + "the user's tasks.\n"
                                        + "Keep the answer friendly and easy "
                                        + "to understand.";

                        // =================================================
                        // CALL GEMINI
                        // =================================================

                        String answer = chatModel.call(prompt);

                        // =================================================
                        // SAVE CHAT HISTORY
                        // =================================================

                        ChatMessage chatMessage = new ChatMessage();

                        chatMessage.setUserEmail(user.getEmail());

                        chatMessage.setUserMessage(
                                        message.trim());

                        chatMessage.setAiResponse(answer);

                        chatMessage.setCreatedAt(
                                        LocalDateTime.now());

                        chatMessageRepository.save(chatMessage);

                        return answer;

                } catch (Exception e) {

                        e.printStackTrace();

                        Throwable cause = e;

                        while (cause.getCause() != null) {
                                cause = cause.getCause();
                        }

                        return "AI Error: "
                                        + cause.getClass().getSimpleName()
                                        + " - "
                                        + cause.getMessage();
                }
        }

        // =========================================================
        // CLEAR CURRENT CHAT
        // Database history will NOT be deleted
        // =========================================================

        @PostMapping("/ai-assistant/clear")
        @ResponseBody
        public String clearChat(
                        HttpSession session) {

                User user = (User) session.getAttribute("loggedInUser");

                if (user == null) {
                        return "Please login first.";
                }

                return "Current chat cleared.";
        }
}