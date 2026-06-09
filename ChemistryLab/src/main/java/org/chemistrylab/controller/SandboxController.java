package org.chemistrylab.controller;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.dto.SandboxSuggestRequest;
import org.chemistrylab.dto.SandboxSuggestResponse;
import org.chemistrylab.service.SandboxSuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sandbox")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SandboxController {

    private final SandboxSuggestionService sandboxSuggestionService;

    @PostMapping("/suggest")
    public ResponseEntity<SandboxSuggestResponse> suggest(@RequestBody SandboxSuggestRequest request) {
        return ResponseEntity.ok(sandboxSuggestionService.suggest(request));
    }
}
