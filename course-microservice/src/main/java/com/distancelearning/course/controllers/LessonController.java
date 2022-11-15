package com.distancelearning.course.controllers;

import com.distancelearning.course.dtos.LessonDto;
import com.distancelearning.course.models.LessonModel;
import com.distancelearning.course.models.ModuleModel;
import com.distancelearning.course.services.LessonService;
import com.distancelearning.course.services.ModuleService;
import com.distancelearning.course.specifications.SpecificationTemplate;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/modules/{moduleId}/lessons")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    private final LessonService lessonService;
    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<Object> saveLesson(@RequestBody @Valid LessonDto lessonDto,
                                             @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found.");
        } else {
            var lessonModel = new LessonModel();
            BeanUtils.copyProperties(lessonDto, lessonModel);
            lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
            lessonModel.setModule(moduleModelOptional.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lessonModel));
        }
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId){
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        } else {
            lessonService.delete(lessonModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("Lesson successfully deleted.");
        }
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto){
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this course.");
        } else {
            var lessonModel = lessonModelOptional.get();
            lessonModel.setTitle(lessonDto.getTitle());
            lessonModel.setDescription(lessonDto.getDescription());
            lessonModel.setVideoUrl(lessonDto.getVideoUrl());
            return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModel));
        }
    }

    @GetMapping
    public ResponseEntity<Page<LessonModel>> getAllLessons(@PathVariable(value = "moduleId") UUID moduleId,
                                                           SpecificationTemplate.LessonSpec spec,
                                                           @PageableDefault(size = 5, sort = "lessonId",
                                                                   direction = Sort.Direction.ASC) Pageable pageable){
        Page<LessonModel> lessonPage = lessonService.findAllByModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable);
        if (!lessonPage.isEmpty()){
            for (LessonModel lessonModel: lessonPage.toList()){
                lessonModel.add(linkTo(methodOn(LessonController.class).getLessonById(moduleId, lessonModel.getLessonId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(lessonPage);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<Object> getLessonById(@PathVariable(value = "moduleId") UUID moduleId,
                                                @PathVariable(value = "lessonId") UUID lessonId){
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        return lessonModelOptional.<ResponseEntity<Object>>map(lesson -> ResponseEntity.status(HttpStatus.OK).body(lesson)).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module."));
    }
}
