package com.danum.danum.service.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {

    private final EndpointLister endpointLister;

    @Value("${discord.channel.api}")
    private String BOT_TOKEN;

    @Value("${discord.channel.id}")
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
                channel.sendMessage("\n# API 명세서\n" + endpointLister.getEndpoints()).queue();
            }
            if (!messages.isEmpty()){
                Message message = messages.get(0);
                message.editMessage("\n# API 명세서\n" + endpointLister.getEndpoints()).queue();
            }
        }
    }

}
