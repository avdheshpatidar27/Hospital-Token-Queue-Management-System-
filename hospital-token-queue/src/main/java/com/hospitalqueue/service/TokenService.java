package com.hospitalqueue.service;

import com.hospitalqueue.model.Token;
import com.hospitalqueue.model.TokenStatus;
import com.hospitalqueue.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

// @Service tells Spring: "create ONE instance of this class and manage it
// for me" (this instance is called a "bean"). Spring puts this bean in a
// container and hands it to anything that asks for it -- that's the whole
// idea of Dependency Injection (DI): classes don't create their own
// dependencies with `new`, Spring hands the dependency to them instead.
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    // ===== Constructor Injection (Dependency Injection in action) =====
    // We are NOT writing `new TokenRepository()` anywhere. Instead, we just
    // declare "I need a TokenRepository" as a constructor parameter, and
    // Spring automatically supplies (injects) the one it manages when it
    // creates this TokenService bean at startup. This is the core idea
    // behind Spring's IoC (Inversion of Control) container.
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // ================= CREATE =================
    public Token createToken(Token token) {
        // Count how many active (non-deleted) tokens exist right now, and
        // use that to assign the next token number. +1 because token
        // numbers start from 1, not 0.
        List<Token> activeTokens = tokenRepository.findByDeletedFalse();
        token.setTokenNumber(activeTokens.size() + 1);

        token.setStatus(TokenStatus.WAITING);
        token.setDeleted(false);
        token.setCreatedAt(LocalDateTime.now());

        return tokenRepository.save(token);
    }

    // ================= READ =================
    public List<Token> getAllActiveTokens() {
        return tokenRepository.findByDeletedFalse();
    }

    public Token getTokenById(Long id) {
        Token token = tokenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found with id: " + id));

        if (token.isDeleted()) {
            throw new RuntimeException("Token with id " + id + " has been deleted");
        }
        return token;
    }

    // ================= UPDATE =================
    public Token updateToken(Long id, Token updatedDetails) {
        Token existingToken = getTokenById(id);

        existingToken.setPatientName(updatedDetails.getPatientName());
        existingToken.setAge(updatedDetails.getAge());
        existingToken.setGender(updatedDetails.getGender());
        existingToken.setDepartment(updatedDetails.getDepartment());
        existingToken.setPriority(updatedDetails.isPriority());

        return tokenRepository.save(existingToken);
    }

    // ================= SOFT DELETE =================
    // Instead of tokenRepository.deleteById(id), we just flip a flag.
    // The row stays in MySQL -- it's just filtered out everywhere else.
    public void softDeleteToken(Long id) {
        Token token = getTokenById(id);
        token.setDeleted(true);
        tokenRepository.save(token);
    }

    public Token restoreToken(Long id) {
        Token token = tokenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found with id: " + id));
        token.setDeleted(false);
        return tokenRepository.save(token);
    }

    // ================= THE QUEUE LOGIC (Collections + DSA) =================

    // Calls the next patient: priority patients go first; among patients
    // with the same priority, whoever got their token FIRST goes next.
    public Token callNextPatient() {
        // Pull every patient still WAITING from the database into a plain
        // Java List.
        List<Token> waitingTokens = tokenRepository.findByDeletedFalseAndStatus(TokenStatus.WAITING);

        if (waitingTokens.isEmpty()) {
            throw new RuntimeException("No patients are currently waiting");
        }

        // A Comparator defines "how to compare two Tokens to decide order."
        // We build ours in two steps, chained with thenComparing:
        //   1) Priority patients first. Since `priority` is a boolean,
        //      we compare Boolean.compare(b.isPriority(), a.isPriority())
        //      so that `true` (priority) sorts BEFORE `false`.
        //   2) If priority is equal, whoever has the SMALLER tokenNumber
        //      (i.e. joined earlier) goes first.
        Comparator<Token> queueOrder = (a, b) -> {
            int priorityCompare = Boolean.compare(b.isPriority(), a.isPriority());
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            return Integer.compare(a.getTokenNumber(), b.getTokenNumber());
        };

        // PriorityQueue is a real Data Structure (a binary heap under the
        // hood) that always keeps the "smallest" element (according to our
        // Comparator) at the front, ready to be removed in O(log n) time.
        // This is the actual DSA concept behind a real-world hospital queue.
        PriorityQueue<Token> queue = new PriorityQueue<>(queueOrder);
        queue.addAll(waitingTokens);

        // poll() removes and returns the highest-priority, longest-waiting
        // patient -- exactly what "call next patient" means in real life.
        Token nextPatient = queue.poll();

        nextPatient.setStatus(TokenStatus.IN_CONSULTATION);
        return tokenRepository.save(nextPatient);
    }

    public Token completeConsultation(Long id) {
        Token token = getTokenById(id);

        if (token.getStatus() != TokenStatus.IN_CONSULTATION) {
            throw new RuntimeException("Token " + id + " is not currently in consultation");
        }

        token.setStatus(TokenStatus.COMPLETED);
        return tokenRepository.save(token);
    }

    // Groups all active WAITING tokens by department, e.g.
    // { "Cardiology": [token1, token2], "General": [token3] }
    // Built with a plain HashMap<String, List<Token>> -- classic Collections
    // Framework usage: no streams, just a loop you can trace step by step.
    public Map<String, List<Token>> getWaitingTokensGroupedByDepartment() {
        List<Token> waitingTokens = tokenRepository.findByDeletedFalseAndStatus(TokenStatus.WAITING);

        Map<String, List<Token>> grouped = new HashMap<>();

        for (Token token : waitingTokens) {
            String department = token.getDepartment();

            // If this department doesn't have a list yet, create one.
            if (!grouped.containsKey(department)) {
                grouped.put(department, new ArrayList<>());
            }

            grouped.get(department).add(token);
        }

        return grouped;
    }
}
