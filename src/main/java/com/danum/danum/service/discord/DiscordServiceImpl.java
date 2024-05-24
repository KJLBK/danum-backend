package com.danum.danum.service.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {

    private final EndpointLister endpointLister;

    @Value("${discord.channel.api.bot}")
    private String BOT_TOKEN;

    @Value("${discord.channel.api.id}")
    private Long CHANNEL_ID;

    @EventListener(ApplicationReadyEvent.class)
    private void sendMessage() throws InterruptedException {
        JDA jda = JDABuilder.createDefault(BOT_TOKEN)
                .build()
                .awaitReady();
        TextChannel channel = jda.getTextChannelById(CHANNEL_ID);

        if (channel != null) {
            List<Message> messages =  channel.getHistory().retrievePast(1).complete();
            if (messages.isEmpty()) {
                channel.sendMessage("API")
                        .setEmbeds(endpointLister.getEndpoints())
                        .queue();
                return;
            }

            Message message = messages.get(0);
            message.editMessage("DANUM API 명세서")
                    .setEmbeds(endpointLister.getEndpoints())
                    .queue();
        }
    }
}
