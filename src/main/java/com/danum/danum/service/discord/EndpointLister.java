package com.danum.danum.service.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EndpointLister {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public List<MessageEmbed> getEndpoints() {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        List<MessageEmbed> embeds = new ArrayList<>();

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("```");

        int endpointCount = 0;
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            if (isMethodAllowed(handlerMethod.getMethod())) {
                String method = String.valueOf(mappingInfo.getMethodsCondition());
                String url = String.valueOf(mappingInfo);
                url = url.substring(url.indexOf("/"), url.indexOf("]"));
                String requestParams = getRequestParams(handlerMethod.getMethod());

                contentBuilder.append("URL : ").append(url).append("\n");
                contentBuilder.append("METHOD : ").append(method).append("\n");
                contentBuilder.append("Parameters : ").append(requestParams).append("\n\n");

                endpointCount++;
                if (endpointCount % 5 == 0) {
                    contentBuilder.append("```");
                    MessageEmbed embed = new EmbedBuilder()
                            .setDescription(contentBuilder.toString())
                            .setColor(Color.ORANGE)
                            .build();
                    embeds.add(embed);
                    contentBuilder.setLength(0);
                    contentBuilder.append("```");
                }
            }
        }

        contentBuilder.append("```");
        MessageEmbed embed = new EmbedBuilder()
                .setDescription(contentBuilder.toString())
                .setColor(Color.GREEN)
                .build();
        embeds.add(embed);

        return embeds;
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
