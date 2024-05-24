package com.danum.danum.service.discord;

import com.danum.danum.exception.DiscordException;
import com.danum.danum.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {

    private static final Integer TEXT_DELETED_LIMIT = 99;

    private final EndpointLister endpointLister;

    @Value("${discord.channel.api.bot}")
    private String botToken;

    @Value("${discord.channel.api.id}")
    private Long messageId;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void sendApiMessage() throws InterruptedException {
        JDA jda = JDABuilder.createDefault(botToken)
                .build()
                .awaitReady();
        TextChannel channel = jda.getTextChannelById(messageId);

        if (channel == null) {
            throw new DiscordException(ErrorCode.DISCORD_CHANNEL_NOT_FOUND);
        }

        MessageHistory history = new MessageHistory(channel);

        List<Message> messageList;

        do {
            messageList = history.retrievePast(TEXT_DELETED_LIMIT)
                    .complete();

            deleteMessage(channel, messageList);
        } while (messageList.size() == TEXT_DELETED_LIMIT);

        Map<Class<?>, List<MessageEmbed>> messageEmbedMap = endpointLister.getEndpoints();

        for (Map.Entry<Class<?>, List<MessageEmbed>> requestMessageMap : messageEmbedMap.entrySet()) {
            channel.sendMessage("## " + requestMessageMap.getKey()
                    .getSimpleName())
                    .queue();

            sendMessageList(channel, requestMessageMap.getValue());
        }

    }

    private void sendMessageList(TextChannel channel, List<MessageEmbed> messageEmbedList) {
        if (messageEmbedList.isEmpty()) {
            return;
        }

        int chunk = 10;
        if (messageEmbedList.size() <= chunk) {
            channel.sendMessageEmbeds(messageEmbedList)
                    .queue();
            return;
        }

        channel.sendMessageEmbeds(messageEmbedList.subList(0, chunk))
                .queue(message -> {
                    sendMessageList(channel, messageEmbedList.subList(chunk, messageEmbedList.size()));
                });
    }

    private void deleteMessage(TextChannel channel, List<Message> messageList) {
        if (messageList.isEmpty()) {
            return;
        }

        int messageSize = messageList.size();
        if (messageSize == 1) {
            String messageId = messageList.get(0).getId();
            channel.deleteMessageById(messageId)
                    .queue();

            return;
        }

        if (messageSize < 100) {
            channel.deleteMessages(messageList)
                    .queue();

            return;
        }

        channel.deleteMessages(messageList.subList(0, TEXT_DELETED_LIMIT))
                .queue();
        deleteMessage(channel, messageList.subList(TEXT_DELETED_LIMIT, messageSize));
    }

}
