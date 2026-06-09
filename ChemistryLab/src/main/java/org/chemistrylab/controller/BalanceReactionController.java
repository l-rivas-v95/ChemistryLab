package org.chemistrylab.controller;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.dto.BalanceReactionRequest;
import org.chemistrylab.dto.BalanceReactionResponse;
import org.chemistrylab.service.BalanceReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reactions")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BalanceReactionController {

    private final BalanceReactionService balanceReactionService;

    @PostMapping("/balance")
    public ResponseEntity<BalanceReactionResponse> balance(@RequestBody BalanceReactionRequest request) {
        return ResponseEntity.ok(balanceReactionService.balance(request));
    }
}
