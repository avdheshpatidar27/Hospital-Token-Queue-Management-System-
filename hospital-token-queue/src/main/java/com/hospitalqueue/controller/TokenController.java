package com.hospitalqueue.controller;

import com.hospitalqueue.model.Token;
import com.hospitalqueue.service.TokenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// @RestController = @Controller + @ResponseBody combined. It tells Spring:
// "every method here handles an HTTP request, and whatever it returns
// should be converted to JSON and sent back as the response body" --
// you never manually write JSON.
@RestController
// Every endpoint in this class starts with /api/tokens
@RequestMapping("/api/tokens")
public class TokenController {

    private final TokenService tokenService;

    // Same Dependency Injection pattern as TokenService -> TokenRepository.
    // Spring creates the TokenService bean first, then hands it to this
    // controller automatically.
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // POST http://localhost:8080/api/tokens
    // @RequestBody tells Spring: "take the incoming JSON body and convert
    // it into a Token object using its setters" (this is called
    // deserialization).
    @PostMapping
    public Token createToken(@RequestBody Token token) {
        return tokenService.createToken(token);
    }

    // GET http://localhost:8080/api/tokens
    @GetMapping
    public List<Token> getAllTokens() {
        return tokenService.getAllActiveTokens();
    }

    // GET http://localhost:8080/api/tokens/3
    // @PathVariable pulls the {id} straight out of the URL.
    @GetMapping("/{id}")
    public Token getTokenById(@PathVariable Long id) {
        return tokenService.getTokenById(id);
    }

    // PUT http://localhost:8080/api/tokens/3
    // PUT = "replace/update this resource's editable details."
    @PutMapping("/{id}")
    public Token updateToken(@PathVariable Long id, @RequestBody Token token) {
        return tokenService.updateToken(id, token);
    }

    // DELETE http://localhost:8080/api/tokens/3
    // Under the hood this is a SOFT delete (see TokenService) -- the row
    // stays in MySQL, it's just marked deleted = true.
    @DeleteMapping("/{id}")
    public String deleteToken(@PathVariable Long id) {
        tokenService.softDeleteToken(id);
        return "Token " + id + " was soft-deleted.";
    }

    // PATCH http://localhost:8080/api/tokens/3/restore
    // PATCH = "make a small, specific change" (as opposed to PUT's full
    // replace) -- here, just flipping deleted back to false.
    @PatchMapping("/{id}/restore")
    public Token restoreToken(@PathVariable Long id) {
        return tokenService.restoreToken(id);
    }

    // PATCH http://localhost:8080/api/tokens/call-next
    // This is the main "queue" action: picks the next patient using the
    // PriorityQueue logic in TokenService and marks them IN_CONSULTATION.
    @PatchMapping("/call-next")
    public Token callNextPatient() {
        return tokenService.callNextPatient();
    }

    // PATCH http://localhost:8080/api/tokens/3/complete
    @PatchMapping("/{id}/complete")
    public Token completeConsultation(@PathVariable Long id) {
        return tokenService.completeConsultation(id);
    }

    // GET http://localhost:8080/api/tokens/grouped-by-department
    @GetMapping("/grouped-by-department")
    public Map<String, List<Token>> getGroupedByDepartment() {
        return tokenService.getWaitingTokensGroupedByDepartment();
    }
}
