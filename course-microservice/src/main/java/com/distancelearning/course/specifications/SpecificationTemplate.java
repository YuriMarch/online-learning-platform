package com.distancelearning.course.specifications;

import com.distancelearning.course.models.CourseModel;
import com.distancelearning.course.models.CourseUserModel;
import com.distancelearning.course.models.LessonModel;
import com.distancelearning.course.models.ModuleModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "courseLevel", spec = Equal.class),
            @Spec(path = "courseStatus", spec = Equal.class),
            @Spec(path = "name", spec = Like.class)
    })
    public interface CourseSpec extends Specification<CourseModel> {}

    @Spec(path = "title", spec = Like.class)
    public interface ModuleSpec extends Specification<ModuleModel> {}

    @Spec(path = "title", spec = Like.class)
    public interface LessonSpec extends Specification<LessonModel> {}

    public static Specification<ModuleModel> moduleCourseId(final UUID courseId){
        return (root, query, cb) -> {
            query.distinct(true);
            Root<ModuleModel> module = root; //Entity A
            Root<CourseModel> course = query.from(CourseModel.class); //Entity B
            Expression<Collection<ModuleModel>> courseModules = course.get("modules"); //collection from Entity A in Entity B
            return cb.and(cb.equal(course.get("courseId"), courseId), cb.isMember(module, courseModules)); //Criteria Builder using AND
        };
    }

    public static Specification<LessonModel> lessonModuleId(final UUID moduleId){
        return (root, query, cb) -> {
            query.distinct(true);
            Root<LessonModel> lesson = root;
            Root<ModuleModel> module = query.from(ModuleModel.class);
            Expression<Collection<LessonModel>> moduleLessons = module.get("lessons");
            return cb.and(cb.equal(module.get("moduleId"), moduleId), cb.isMember(lesson, moduleLessons));
        };
    }

    //Query to connect CourseModel and CourseUserModel
    public static Specification<CourseModel> courseUserId (final UUID userId){
        return (root, query, cb) -> {
            query.distinct(true);
            Join<CourseModel, CourseUserModel> courseProd = root.join("coursesUsers");
            return cb.equal(courseProd.get("userId"), userId);
        };
    }
}
