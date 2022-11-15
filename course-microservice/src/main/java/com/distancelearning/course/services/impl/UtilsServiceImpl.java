package com.distancelearning.course.services.impl;

import com.distancelearning.course.services.UtilsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

    @Value("${distancelearning.api.url.authuser}")
    String REQUEST_URL_AUTHUSER;

    public String createUrlGetAllUsersInCourse(UUID courseId, Pageable pageable) {
        return REQUEST_URL_AUTHUSER + "/users?courseId=" + courseId + "&page=" + pageable.getPageNumber() + "&size="
                + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }

    public String createUrlGetOneUserById(UUID userId) {
        return REQUEST_URL_AUTHUSER + "/users/" + userId;
    }
}
