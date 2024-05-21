package com.danum.danum.service.discord;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EndpointLister {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public String getEndpoints() {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        return map.entrySet().stream()
                .map(entry -> {
                    try {
                        RequestMappingInfo mappingInfo = entry.getKey();
                        if (!isMethodAllowed(entry.getValue().getMethod())) {
                            return null;
                        }
                        String requestParams = getRequestParams(entry.getValue().getMethod());

                        return String.format("```%s\n%s```",
                                mappingInfo, requestParams);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
    }

    private boolean isMethodAllowed(Method method) {
        return method.isAnnotationPresent(GetMapping.class) ||
                method.isAnnotationPresent(PostMapping.class) ||
                method.isAnnotationPresent(PutMapping.class) ||
                method.isAnnotationPresent(DeleteMapping.class);
    }

    private String getRequestParams(Method method) {
        Parameter[] parameters = method.getParameters();
        StringBuilder paramsInfo = new StringBuilder();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                paramsInfo.append("RequestBody: ").append(parameter.getType().getSimpleName()).append("\n");
                paramsInfo.append(getFieldInfo(parameter.getType()));
            }
        }

        return paramsInfo.toString();
    }

    private String getFieldInfo(Class<?> clazz) {
        StringBuilder fieldInfo = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            fieldInfo.append("  - ").append(field.getName()).append(": ").append(field.getType().getSimpleName()).append("\n");
        }

        return fieldInfo.toString();
    }

}
