package com.danum.danum.service.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EndpointLister {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public Map<Class<?>, List<MessageEmbed>> getEndpoints() {
        Map<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<Class<?>, Map<RequestMappingInfo, HandlerMethod>> controllerPerRequest = new HashMap<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingInfoHandlerMethodMap.entrySet()) {
            Class<?> controllerClass = entry.getValue().getBeanType();

            if (entry.getKey().equals("BasicErrorController")) {
                continue;
            }

            controllerPerRequest.computeIfAbsent(controllerClass, k -> new HashMap<>())
                    .put(entry.getKey(), entry.getValue());
        }

        Map<Class<?>, List<MessageEmbed>> result = new HashMap<>();

        for (Class<?> key : controllerPerRequest.keySet()) {
            Map<RequestMappingInfo, HandlerMethod> requestMappingMap = controllerPerRequest.get(key);

            List<MessageEmbed> messageEmbedList = new ArrayList<>();

            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingMap.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();

                Color color = getColor(handlerMethod.getMethod());
                if (color == null) {
                    continue;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();

                String method = mappingInfo.getMethodsCondition().toString();

                String url = mappingInfo.getActivePatternsCondition()
                        .toString();
                url = url.substring(url.indexOf("/"), url.indexOf("]"));

                Method requestParams = handlerMethod.getMethod();

                embedBuilder.setDescription(requestParams.getName());
                embedBuilder.setAuthor(method);
                embedBuilder.setTitle(url);

                embedBuilder.addField("파라미터", getRequestParams(requestParams), false);
                embedBuilder.addField("반환 값", requestParams.getGenericReturnType() + "\n" + requestParams.getGenericReturnType().getTypeName(), false);

                embedBuilder.setTimestamp(OffsetDateTime.now());
                embedBuilder.setColor(color);
                messageEmbedList.add(embedBuilder.build());
            }

            result.put(key, messageEmbedList);
        }

        return result;
    }

    private Color getColor(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return new Color(60, 179, 113);
        }

        if (method.isAnnotationPresent(PostMapping.class)) {
            return new Color(255, 165, 0);
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            return new Color(75, 137, 220);
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return Color.RED;
        }

        if (method.isAnnotationPresent(PatchMapping.class)) {
            return new Color(150, 123, 220);
        }

        return null;
    }

    private String getRequestParams(Method method) {
        Parameter[] parameters = method.getParameters();
        StringBuilder paramsInfo = new StringBuilder();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                paramsInfo.append("RequestBody: ")
                        .append(parameter.getType()
                                .getSimpleName())
                        .append("\n");
                paramsInfo.append(getFieldInfo(parameter.getType()));
            }
        }

        return paramsInfo.toString();
    }

    private String getFieldInfo(Class<?> clazz) {
        StringBuilder fieldInfo = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            fieldInfo.append("- ")
                    .append(field.getName())
                    .append(": ")
                    .append(field.getType()
                            .getSimpleName())
                    .append("\n");
        }

        return fieldInfo.toString();
    }

}
