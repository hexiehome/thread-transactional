package com.hexiehome.transactional.controller;

import com.hexiehome.transactional.domain.ScenarioInfo;
import com.hexiehome.transactional.domain.ScenarioRequest;
import com.hexiehome.transactional.domain.ScenarioResult;
import com.hexiehome.transactional.domain.ScenarioType;
import com.hexiehome.transactional.server.ThreadTransactionalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scenarios")
public class SaveDataController {

    private final ThreadTransactionalService service;

    public SaveDataController(ThreadTransactionalService service) {
        this.service = service;
    }

    @GetMapping
    public List<ScenarioInfo> listScenarios() {
        return service.listScenarios();
    }

    @PostMapping("/run")
    public ScenarioResult runScenario(@RequestBody ScenarioRequest request) {
        return service.runScenario(request);
    }

    // Backward-compatible quick trigger for old demo callers.
    @GetMapping("/legacy")
    public ScenarioResult createUser(@RequestParam(defaultValue = "10") Integer size) {
        return service.runScenario(new ScenarioRequest(ScenarioType.ALL_SUCCESS, size, 5, 10));
    }
}
