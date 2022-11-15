package com.distancelearning.course.services.impl;

import com.distancelearning.course.clients.AuthUserClient;
import com.distancelearning.course.models.CourseModel;
import com.distancelearning.course.models.CourseUserModel;
import com.distancelearning.course.models.LessonModel;
import com.distancelearning.course.models.ModuleModel;
import com.distancelearning.course.repositories.CourseRepository;
import com.distancelearning.course.repositories.CourseUserRepository;
import com.distancelearning.course.repositories.LessonRepository;
import com.distancelearning.course.repositories.ModuleRepository;
import com.distancelearning.course.services.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    private final CourseUserRepository courseUserRepository;

    private final AuthUserClient authUserClient;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {
        boolean deleteCourseUserInAuthUser = false;
        List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());
        if (!moduleModelList.isEmpty()){
            for (ModuleModel module : moduleModelList){
                List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());
                if (!lessonModelList.isEmpty()){
                    lessonRepository.deleteAll(lessonModelList);
                }
            }
            moduleRepository.deleteAll(moduleModelList);
        }
        List<CourseUserModel> courseUserModelList = courseUserRepository.findAllCourseUserIntoCourse(courseModel.getCourseId());
        if (!courseUserModelList.isEmpty()){
            courseUserRepository.deleteAll(courseUserModelList);
            deleteCourseUserInAuthUser = true;
        }
        courseRepository.delete(courseModel);
        if (deleteCourseUserInAuthUser){
            authUserClient.deleteCourseInAuthUser(courseModel.getCourseId());
        }
    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return courseRepository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public List<CourseModel> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return courseRepository.findAll(spec, pageable);
    }
}
