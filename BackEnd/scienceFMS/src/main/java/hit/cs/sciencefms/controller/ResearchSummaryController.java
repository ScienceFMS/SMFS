package hit.cs.sciencefms.controller;

import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.service.ResearchSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher/research-summary")
public class ResearchSummaryController {

    @Autowired
    private ResearchSummaryService researchSummaryService;
    
    @GetMapping("/generate/{teacherId}")
    public Result<String> generateSummaryByTeacherId(@PathVariable String teacherId) {
        try {
            String summary = researchSummaryService.generateSummaryByTeacherId(teacherId);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
} 